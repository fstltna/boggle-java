/*
 * DictionaryUnavailableException
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-11
 *
 * bluebones.net Boggle - network-aware multiplayer word game.
 * Copyright (C) 2004-5 Thomas David Baker <bakert@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.bluebones.boggle;

/**
 * Exception thrown when the dictionary of acceptable words is not available for
 * any reason.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class DictionaryUnavailableException extends Throwable {

    /**
     * Constructs a new DictionaryUnavailableException with <code>null</code> 
     * as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public DictionaryUnavailableException() {
        super();
    }

    /**
     * Constructs a new DictionaryUnavailableException with the specified 
     * detail message.  
     * The cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param   message     the detail message. The detail message is saved for 
     *                      later retrieval by the {@link #getMessage()} method.
     */
    public DictionaryUnavailableException(String message) {
        super(message);
    }

    /**
     * Constructs a new DictionaryUnavailableException with the specified 
     * detail message and cause.  <p>Note that the detail message associated 
     * with <code>cause</code> is <i>not</i> automatically incorporated in
     * this DictionaryUnavailableException's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public DictionaryUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DictionaryUnavailableException with the specified cause 
     * and a detail message of 
     * <tt>(cause==null &#63; null : cause.toString())</tt> 
     * (which typically contains the class and detail message of 
     * <tt>cause</tt>).
     * This constructor is useful for DictionaryUnavailableExceptions that are 
     * little more than wrappers for other throwables.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public DictionaryUnavailableException(Throwable cause) {
        super(cause);
    }
}
