package hk.hku.cecid.edi.as2.util;

import hk.hku.cecid.edi.as2.AS2PlusProcessor;

public class AS2Util {
    public static String getFileNameFromMIMEHeader(String[] contentDispositions) {
		if (contentDispositions == null || contentDispositions.length == 0) {
			return null;
		}
		
		for (String value : contentDispositions) {
			String[] tokens = value.split(";");
			if (tokens == null || tokens.length <= 1) {
				continue;
			}
			if (!"attachment".equalsIgnoreCase(tokens[0].trim())) {
				continue;
			}
			for (int i=1; i < tokens.length; i++) {
				if (tokens[i].trim().startsWith("filename")) {
					String fileName = tokens[i].substring(tokens[i].indexOf("=") + 1);
					if (fileName.trim().length() > 0) {
						AS2PlusProcessor.getInstance().getLogger().debug("Filename found: " + fileName);
						return fileName;
					}
				}
			}
		}
		
		return null;
    }
}
