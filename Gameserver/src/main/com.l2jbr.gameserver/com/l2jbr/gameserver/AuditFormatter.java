/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jbr.gameserver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


/**
 * @author zabbix Lets drink to code!
 */
public class AuditFormatter extends Formatter {
    private static final String CRLF = "\r\n";
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("dd MMM H:mm:ss");

    @Override
    public String format(LogRecord record) {
        StringBuilder output = new StringBuilder();
        output.append('[');
        output.append(dateFmt.format(new Date(record.getMillis())));
        output.append(']');
        output.append(' ');
        output.append(record.getMessage());
        for (Object p : record.getParameters()) {
            if (p == null) {
                continue;
            }
            output.append(',');
            output.append(' ');
            output.append(p.toString());
        }
        output.append(CRLF);

        return output.toString();
    }
}
