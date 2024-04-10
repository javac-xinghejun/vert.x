/*
 * Copyright (c) 2011-2024 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.net.impl;

import io.netty.channel.EventLoop;
import io.vertx.core.ThreadingModel;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.streams.impl.InboundReadQueue;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

public class InboundMessageQueue<M> implements Predicate<M>, Runnable {

  private final ContextInternal context;
  private final EventLoop eventLoop;
  private final InboundReadQueue<M> readQueue;
  private final AtomicLong demand = new AtomicLong(Long.MAX_VALUE);
  private boolean draining;

  public InboundMessageQueue(EventLoop eventLoop, ContextInternal context) {
    InboundReadQueue.Factory readQueueFactory;
    if (context.threadingModel() == ThreadingModel.EVENT_LOOP && context.nettyEventLoop() == eventLoop) {
      readQueueFactory = InboundReadQueue.SINGLE_THREADED;
    } else {
      readQueueFactory = InboundReadQueue.SPSC;
    }
    this.readQueue = readQueueFactory.create(this);
    this.context = context;
    this.eventLoop = eventLoop;
  }

  @Override
  public boolean test(M msg) {
    while (true) {
      long d = demand.get();
      if (d == 0L) {
        return false;
      } else if (d == Long.MAX_VALUE || demand.compareAndSet(d, d - 1)) {
        break;
      }
    }
    handleMessage(msg);
    return true;
  }

  /**
   * Handle resume, executed on the context thread.
   */
  protected void handleResume() {
  }

  /**
   * Handler pause, executed on the event-loop thread
   */
  protected void handlePause() {
  }

  /**
   * Handle a message.
   *
   * @param msg the message
   */
  protected void handleMessage(M msg) {
  }

  public boolean add(M msg) {
    assert eventLoop.inEventLoop();
    int res = readQueue.add(msg);
    if ((res & InboundReadQueue.QUEUE_UNWRITABLE_MASK) != 0) {
      handlePause();
    }
    return (res & InboundReadQueue.DRAIN_REQUIRED_MASK) != 0;
  }

  public void write(M msg) {
    if (add(msg)) {
      drain();
    }
  }

  @Override
  public void run() {
    if (draining) {
      return;
    }
    draining = true;
    try {
      int drain = readQueue.drain();
      if ((drain & InboundReadQueue.QUEUE_WRITABLE_MASK) != 0) {
        eventLoop.execute(this::handleResume);
      }
    } finally {
      draining = false;
    }
  }

  public void drain() {
    if (context.inThread()) {
      run();
    } else {
      context.execute(this);
    }
  }

  public void pause() {
    demand.set(0L);
  }

  public void demand(long value) {
    if (value < 0L) {
      throw new IllegalArgumentException();
    }
    demand.set(value);
    if (value > 0L) {
      context.executor().execute(this); // TODO TEST THIS
    }
  }

  public void fetch(long amount) {
    if (amount < 0L) {
      throw new IllegalArgumentException();
    }
    if (amount == 0L) {
      return;
    }
    while (true) {
      long prev = demand.get();
      long next = prev + amount;
      if (next < 0L) {
        next = Long.MAX_VALUE;
      }
      if (prev == next || demand.compareAndSet(prev, next)) {
        break;
      }
    }
    context.executor().execute(this); // TODO TEST THIS
  }
}
