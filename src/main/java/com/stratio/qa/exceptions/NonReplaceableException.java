/*
 * Copyright (c) 2014. Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software is licensed under the Apache Licence 2.0. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the terms of the License for more details.
 * SPDX-License-Identifier: Artistic-2.0
 */

package com.stratio.qa.exceptions;

public class NonReplaceableException extends Exception {

    public NonReplaceableException(String message) {
        super(message);
    }

    public NonReplaceableException() {
        super();
    }

    public NonReplaceableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonReplaceableException(Throwable cause) {
        super(cause);
    }

}