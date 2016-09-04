/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.dao;

import java.math.BigDecimal;
import junit.framework.TestCase;

public class AbstractDVOTest extends TestCase {
	
	private AbstractDVO dvo = new AbstractDVO(){};
	
	// Invoked for setup.
	public void setUp() throws Exception {
		System.out.println();
		System.out.println("---------" + this.getName() + " Start -------");
	}
	
	// Invoked for finalized.
	public void tearDown() throws Exception {
		System.out.println("---------" + this.getName() + " End   -------");
	}	
	
	public void testString() throws Exception {
		dvo.setString("string1", "TESTING");
		TestCase.assertEquals("TESTING", dvo.getString("string1"));
		
		TestCase.assertEquals(null, dvo.getString("string2"));
	}
	
	public void testInt() throws Exception {
		int i = 12345678;
		
		dvo.setInt("int1", i);
		TestCase.assertEquals(i, dvo.getInt("int1"));
		
		dvo.getData().put("int2", new Integer(i));
		TestCase.assertEquals(i, dvo.getInt("int2"));

		dvo.getData().put("int3", new Integer(i));
		TestCase.assertEquals(Integer.toString(i), dvo.getString("int3"));

		dvo.getData().put("int4", new BigDecimal(i));
		TestCase.assertEquals(i, dvo.getInt("int4"));

		TestCase.assertEquals(Integer.MIN_VALUE, dvo.getInt("int5"));
	}

	public void testLong() throws Exception {
		long l = 1234567890123456789L;
		dvo.setLong("long1", l);
		TestCase.assertEquals(l, dvo.getLong("long1"));
		
		dvo.setLong("long2", l);
		TestCase.assertEquals(Long.toString(l), dvo.getString("long2"));

		dvo.getData().put("long3", new Long(l));
		TestCase.assertEquals(l, dvo.getLong("long3"));

		dvo.getData().put("long4", new BigDecimal(l));
		TestCase.assertEquals(l, dvo.getLong("long4"));

		TestCase.assertEquals(Long.MIN_VALUE, dvo.getLong("long5"));
	}
	
	public void testDouble() throws Exception {
		double d = 1.23456789F;
		
		dvo.setDouble("double1", d);
		TestCase.assertTrue(dvo.getDouble("double1") <= d && dvo.getDouble("double1") >= d);
		
		dvo.setDouble("double2", d);
		TestCase.assertTrue(dvo.getString("double2").startsWith("1.23456788"));

		dvo.getData().put("double3", new Double(d));
		TestCase.assertTrue(dvo.getDouble("double3") <= 1.23456788F && dvo.getDouble("double3") >= 1.23456790F);

		dvo.getData().put("double4", new BigDecimal(d));
		TestCase.assertTrue(dvo.getDouble("double4") <= 1.23456788F && dvo.getDouble("double4") >= 1.23456790F);

		TestCase.assertEquals(Double.NaN, dvo.getDouble("double5"));
	}	

	public void testBoolean() throws Exception {
		dvo.setBoolean("boolean1", true);
		TestCase.assertEquals(true, dvo.getBoolean("boolean1"));
		
		dvo.setBoolean("boolean2", true);
		TestCase.assertEquals("true", dvo.getString("boolean2"));

		dvo.getData().put("boolean3", new Boolean(true));
		TestCase.assertEquals(true, dvo.getBoolean("boolean3"));

		TestCase.assertEquals(false, dvo.getBoolean("boolean4"));
	}		
	
	public void testTimestamp() throws Exception {
		java.util.Date today = new java.util.Date();
		
		dvo.setDate("date1", today);
		TestCase.assertTrue(today.equals(dvo.getTimestamp("date1")));
		TestCase.assertTrue(today.equals(dvo.getDate("date1")));
		
		java.sql.Timestamp date2 = new java.sql.Timestamp(today.getTime());
		dvo.setDate("date2", date2);
		TestCase.assertTrue(today.equals(dvo.getTimestamp("date2")));
		TestCase.assertTrue(today.equals(dvo.getDate("date2")));

		dvo.getData().put("date3", new oracle.sql.TIMESTAMP(date2));
		TestCase.assertTrue(today.equals(dvo.getTimestamp("date3")));
		TestCase.assertTrue(today.equals(dvo.getDate("date3")));
		
		java.sql.Date date4 = new java.sql.Date(today.getTime());
		dvo.setDate("date4", date4);
		TestCase.assertTrue(today.equals(dvo.getTimestamp("date4")));
		TestCase.assertTrue(today.equals(dvo.getDate("date4")));
		
		/*
		// Oracle DATE would truncate millisecond
		dvo.getData().put("date5", new oracle.sql.DATE(date2));
		System.out.println(date2.getTime());
		System.out.println(new oracle.sql.DATE(date2).timestampValue().getTime());
		TestCase.assertTrue(date2.equals(dvo.getDate("date5")));
		*/
	}
	
	public void testBlob() throws Exception {
		String s = "abcdefghijklmnopqrstuvwxzy";
		
		dvo.put("byte1", s.getBytes());
		TestCase.assertTrue(s.equals(new String((byte[])dvo.get("byte1"))));
		
		dvo.setString("byte2", s);
		TestCase.assertEquals(s, (String)dvo.get("byte2"));

	}

}
