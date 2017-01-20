package hk.hku.cecid.edi.sfrm.archive;

import java.io.File;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarUtils;

import java.lang.reflect.Field;

/**
 * @author Patrick
 *
 */
public class SFRMTarEntry extends TarEntry {

    /**
     * Construct an entry with only a name. This allows the programmer
     * to construct the entry's header "by hand". File is set to null.
     *
     * @param name the entry name
     */
    public SFRMTarEntry(String name) {
       super(name);

    }

    /**
     * Construct an entry with a name an a link flag.
     *
     * @param name the entry name
     * @param linkFlag the entry link flag.
     */
    public SFRMTarEntry(String name, byte linkFlag) {
        super(name, linkFlag);
    }

    /**
     * Construct an entry for a file. File is set to file, and the
     * header is constructed from information from the file.
     *
     * @param file The file that the entry represents.
     */
    public SFRMTarEntry(File file) {
        super(file);
    }

    /**
     * Construct an entry from an archive's header bytes. File is set
     * to null.
     *
     * @param headerBuf The header bytes from a tar archive entry.
     */
    public SFRMTarEntry(byte[] headerBuf) {
        super(headerBuf);
    }
    
    /**
     * Write an entry's header information to a header buffer.
     *
     * @param outbuf The tar entry header buffer to fill in.
     */
    public void writeEntryHeader(byte[] outbuf) {
        int offset = 0;

        offset = SFRMTarUtils.getNameBytes(new StringBuffer(this.getName()), outbuf, offset, NAMELEN);
        offset = TarUtils.formatOctalBytes(this.getMode(), outbuf, offset, MODELEN);
        offset = TarUtils.formatOctalBytes(this.getUserId(), outbuf, offset, UIDLEN);
        offset = TarUtils.formatOctalBytes(this.getGroupId(), outbuf, offset, GIDLEN);
        offset = TarUtils.formatLongOctalBytes(this.getSize(), outbuf, offset, SIZELEN);
        offset = TarUtils.formatLongOctalBytes(this.getModTime().getTime(), outbuf, offset, MODTIMELEN);

        int csOffset = offset;

        for (int c = 0; c < CHKSUMLEN; ++c) {
            outbuf[offset++] = (byte) ' ';
        }
        try{
	        outbuf[offset++] = getPrivateVariable("linkFlag").getByte(this);
	        offset = SFRMTarUtils.getNameBytes(new StringBuffer(this.getLinkName()), outbuf, offset, NAMELEN);
	        offset = SFRMTarUtils.getNameBytes((StringBuffer) getPrivateVariable("magic").get(this), outbuf, offset, MAGICLEN);
	        offset = SFRMTarUtils.getNameBytes(new StringBuffer(this.getUserName()), outbuf, offset, UNAMELEN);
	        offset = SFRMTarUtils.getNameBytes(new StringBuffer(this.getGroupName()), outbuf, offset, GNAMELEN);
	        offset = TarUtils.formatOctalBytes(getPrivateVariable("devMajor").getInt(this), outbuf, offset, DEVLEN);
	        offset = TarUtils.formatOctalBytes(getPrivateVariable("devMinor").getInt(this), outbuf, offset, DEVLEN);
        }catch(Exception e){
        	System.err.println("Error when getting the private variable from TarEntry");
        }
        
        while (offset < outbuf.length) {
            outbuf[offset++] = 0;
        }

        long chk = TarUtils.computeCheckSum(outbuf);

        TarUtils.formatCheckSumOctalBytes(chk, outbuf, csOffset, CHKSUMLEN);
    }
    
    /**
     * Parse an entry's header information from a header buffer.
     *
     * @param header The tar entry header buffer to get information from.
     */
    public void parseTarHeader(byte[] header) {
        int offset = 0;
        
        this.setName(SFRMTarUtils.parseName(header, offset, NAMELEN));
        offset += NAMELEN;
        
        this.setMode((int) TarUtils.parseOctal(header, offset, MODELEN));
        offset += MODELEN;
        
        this.setUserId((int) TarUtils.parseOctal(header, offset, UIDLEN));
        offset += UIDLEN;
        
        this.setGroupId((int) TarUtils.parseOctal(header, offset, GIDLEN));
        offset += GIDLEN;
        
        this.setSize(TarUtils.parseOctal(header, offset, SIZELEN));
        offset += SIZELEN;
        
        this.setModTime(TarUtils.parseOctal(header, offset, MODTIMELEN));
        offset += MODTIMELEN;
        
        offset += CHKSUMLEN;
        
        try{
	        getPrivateVariable("linkFlag").setByte(this, header[offset++]);
	        
	        getPrivateVariable("linkName").set(this, new StringBuffer(SFRMTarUtils.parseName(header, offset, NAMELEN)));
	        offset += NAMELEN;
	        
	        getPrivateVariable("magic").set(this, new StringBuffer(SFRMTarUtils.parseName(header, offset, MAGICLEN)));
	        offset += MAGICLEN;
	        
	        this.setUserName(SFRMTarUtils.parseName(header, offset, UNAMELEN));
	        offset += UNAMELEN;
	        
	        this.setGroupName(SFRMTarUtils.parseName(header, offset, GNAMELEN));
	        offset += GNAMELEN;
	        
	        getPrivateVariable("devMajor").setInt(this, (int)TarUtils.parseOctal(header, offset, DEVLEN));
	        offset += DEVLEN;

	        getPrivateVariable("devMinor").setInt(this, (int)TarUtils.parseOctal(header, offset, DEVLEN));
        } catch(Exception e) {
        	System.err.println("Error when getting private variable from TarEntry");
        }
        
    }
    
    private Field getPrivateVariable(String name) {
    	try {
    		Class clazz = this.getClass().getSuperclass();
	    	Field field = clazz.getDeclaredField(name);
	    	field.setAccessible(true);
	    	return field;
    	} catch(Exception e) {
    		System.out.println("return null");
    		return null;
    	}
    }
}
