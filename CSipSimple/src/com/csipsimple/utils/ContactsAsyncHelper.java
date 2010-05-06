/**
 * Copyright (C) 2010 Regis Montoya (aka r3gis - www.r3gis.fr)
 * Copyright (C) 2008 The Android Open Source Project
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.csipsimple.utils;

import android.os.Handler;

public class ContactsAsyncHelper extends Handler {
	/**
     * Interface for a WorkerHandler result return.
     */
    public interface OnImageLoadCompleteListener {
        /**
         * Called when the image load is complete.
         * 
         * @param imagePresent true if an image was found
         */  
        public void onImageLoadComplete(int token, Object cookie, ImageView iView,
                boolean imagePresent);
    }
     

}