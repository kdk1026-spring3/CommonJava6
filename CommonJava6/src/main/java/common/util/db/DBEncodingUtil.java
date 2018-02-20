package common.util.db;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBEncodingUtil {
	
	private DBEncodingUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DBEncodingUtil.class);
	
	private static final String ISO_8859_1 = "8859_1";
	private static final String EUC_KR = "euc-kr";

	public static class US7ASCII {
		
		private US7ASCII() {
			super();
		}

		public static String readHangeul(String val) {
			String sVal = "";
			try {
				sVal = new String(val.getBytes(ISO_8859_1), EUC_KR);
			} catch (UnsupportedEncodingException e) {
				logger.error("readHangeul UnsupportedEncodingException", e);
			}
			return sVal;
		}
		
		public static void readHangeul(Map<Object, Object> map, String ... keys) {
			String key = "";
			String val = "";
			
			for (int i=0; i < keys.length; i++) {
				key = keys[i];
				
				if (map.containsKey(key)) {
					val = readHangeul( String.valueOf(map.get(key)) );
					map.put(key, val);
				}
			}
		}
		
		public static void readHangeul(List<Map<Object, Object>> list, String ... keys) {
			for (Map<Object, Object> map : list) {
				readHangeul(map, keys);
			}
		}
		
		public static void writeHangeul(Map<Object, Object> map, String ... keys) {
			String key = "";
			String val1 = "";
			String val2 = "";
			
			for (int i=0; i < keys.length; i++) {
				key = keys[i];
				
				if (map.containsKey(key)) {
					val1 = String.valueOf(map.get(key));
					
					try {
						val2 = new String(val1.getBytes(EUC_KR), ISO_8859_1);
						
					} catch (UnsupportedEncodingException e) {
						logger.error("readHangeul UnsupportedEncodingException", e);
					}

					map.put(key, val2);
				}
			}
		}
	}
	
}
