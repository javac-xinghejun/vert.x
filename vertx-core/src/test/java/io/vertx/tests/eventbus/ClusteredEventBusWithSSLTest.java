/*
 * Copyright (c) 2011-2019 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package io.vertx.tests.eventbus;

import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.test.tls.Cert;
import io.vertx.test.tls.Trust;


/**
 * Tests the clustered event bus with various SSL / TLS configuration.
 *
 * @author Clement Escoffier
 */
public class ClusteredEventBusWithSSLTest extends ClusteredEventBusTestBase {

  private final EventBusOptions options;

  public ClusteredEventBusWithSSLTest() {
    options = new EventBusOptions()
      .setSsl(true)
      .setTrustOptions(Trust.SERVER_JKS.get())
      .setKeyCertOptions(Cert.SERVER_JKS.get());
  }

  @Override
  protected void startNodes(int numNodes) {
    super.startNodes(numNodes, new VertxOptions().setEventBusOptions(options));
  }
}
