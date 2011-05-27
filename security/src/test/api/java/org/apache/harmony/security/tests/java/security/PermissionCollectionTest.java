/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
* @author Alexey V. Varlamov
*/

package org.apache.harmony.security.tests.java.security;
import java.security.*;
import java.util.*;

import junit.framework.TestCase;


/**
 * Tests for <code>PermissionCollection</code>
 *
 */

public class PermissionCollectionTest extends TestCase {

    /**
     * Constructor for PermissionCollectionTest.
     * @param arg0
     */
    public PermissionCollectionTest(String arg0) {
        super(arg0);
    }

    // Bare extension to instantiate abstract PermissionCollection class
    private static final class RealPermissionCollection extends PermissionCollection
    {
        final private Collection col;
        public RealPermissionCollection(Collection col)
        {
            this.col = col;
        }

        public void add(Permission permission) {}

        public Enumeration elements()
        {
            return col == null ? null : Collections.enumeration(col);
        }

        public boolean implies(Permission permission)
        {
            return false;
        }
    }

    /** Test read-only flag. Should be false by default and can be set once forever. */
    public void testReadOnly()
    {
        PermissionCollection pc = new RealPermissionCollection(null);
        assertFalse("should not be read-only by default", pc.isReadOnly());
        pc.setReadOnly();
        assertTrue("explicitly set read-only", pc.isReadOnly());
        pc.setReadOnly();
        assertTrue("more calls to setReadOnly() should not harm", pc.isReadOnly());
    }

    // Regression test for "exitVM' special treatement in Java 6.
    public void test_implies_exitVM() {

        RuntimePermission runtimePermission = new RuntimePermission("exitVM");
        PermissionCollection permissionCollection = runtimePermission
                .newPermissionCollection();
        permissionCollection.add(runtimePermission);
        assertFalse(permissionCollection.implies(new RuntimePermission(
                "exitVM.")));
        assertTrue(permissionCollection.implies(new RuntimePermission(
                "exitVM.1")));
        assertTrue(permissionCollection.implies(new RuntimePermission(
                "exitVM.teststring")));

        runtimePermission = new RuntimePermission("exitVM.*");
        permissionCollection = runtimePermission.newPermissionCollection();
        permissionCollection.add(runtimePermission);
        assertTrue(permissionCollection
                .implies(new RuntimePermission("exitVM")));
        assertFalse(permissionCollection.implies(new RuntimePermission(
                "exitVMteststring")));
        assertFalse(permissionCollection.implies(new RuntimePermission(
                "exitVM.")));
        assertTrue(permissionCollection.implies(new RuntimePermission(
                "exitVM.1")));
        assertTrue(permissionCollection.implies(new RuntimePermission(
                "exitVM.teststring")));
    }
}