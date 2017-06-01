/**
 * Copyright (C) 2011 - Alexey Kolenko
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

package com.belsoft;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessage {

    public static final int USER_NOT_FOUND = 0x80000001;
    public static final int RECEIVER_NAME_IS_EMPTY = 0x80000002;
    public static final int MESSAGE_TEXT_IS_EMPTY = 0x80000003;
    public static final int SUBJECT_TEXT_IS_EMPTY = 0x80000004;

    private static Map<Object,String>mp;
    
    public ErrorMessage() {
        this.mp=new HashMap<Object, String>();
        this.mp.put(new Integer(USER_NOT_FOUND), "Пользователя с таким именем не существует");
        this.mp.put(new Integer(RECEIVER_NAME_IS_EMPTY), "Имя получателя не может быть пустым");
        this.mp.put(new Integer(MESSAGE_TEXT_IS_EMPTY), "Сообщение в письме не должно быть пустым");
        this.mp.put(new Integer(SUBJECT_TEXT_IS_EMPTY), "Тема письма не должна быть пустой");
    }

    public static String getError(int errorId) {
        String s = mp.get(errorId);
        if (s != null)
            return s;
        else
            return "Unknown error code "+Integer.toHexString(errorId); 
    }
    
}

