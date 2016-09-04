/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * IOHandler provides some convenient methods for handling some basic input and 
 * output funtions.  
 * 
 * @author Hugo Y. K. Lam
 * 
 */
public final class IOHandler {

    private static final int BUFFER_SIZE = 512;
    
    /**
     * Creates a new instance of IOHandler.
     */
    private IOHandler() {
    }

    /**
     * Merges the given input streams into one input stream.
     * 
     * @param ins1 the first input stream to be merged.
     * @param ins2 the second input stream to be merged.
     * @return the merged input stream.
     */
    public static InputStream merge(InputStream ins1, InputStream ins2) {
        return new MergedInputStream(ins1, ins2);
    }
    
    /**
     * Pipes an input stream to an output stream. 
     * 
     * @param ins the input stream to be read.
     * @param out the output stream to be written.
     * @throws IOException if there is IO error occurred during the operation.
     */
    public static void pipe(InputStream ins, OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        int len = ins.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = ins.read(buffer);
        }
        out.flush();
    }
    
    /**
     * Pipes a reader to a writer. 
     * 
     * @param reader the reader to be read.
     * @param writer the writer to be written.
     * @throws IOException if there is IO error occurred during the operation.
     */
    public static void pipe(Reader reader, Writer writer) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];

        int len = reader.read(buffer);
        while (len != -1) {
            writer.write(buffer, 0, len);
            len = reader.read(buffer);
        }
        writer.flush();
    }

    /**
     * Reads an array of bytes from an input stream.
     * 
     * @param ins the input stream to be read.
     * @return an array of bytes read from the specified input stream.
     * @throws IOException if there is IO error occurred during the operation.
     */
    public static byte[] readBytes(InputStream ins) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(ins.available());
        pipe(ins, out);
        byte[] bytes = out.toByteArray();
        out.close();
        return bytes;
    }
    
    /**
     * Reads an array of bytes from a reader.
     * 
     * @param reader the reader to be read.
     * @param charset the charset used to convert the characters. 
     * @return an array of bytes read from the specified reader.
     * @throws IOException if there is IO error occurred during the operation.
     */
    public static byte[] readBytes(Reader reader, Charset charset) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out, charset); 
        pipe(reader, writer);
        byte[] bytes = out.toByteArray();
        writer.close();
        out.close();
        return bytes;
    }
    
    /**
     * Reads a string from an input stream.
     * 
     * @param ins the input stream to be read.
     * @param charset the charset used to convert the bytes.
     * @return a string read from the specified input stream.
     * @throws IOException if there is IO error occurred during the operation.
     */
    public static String readString(InputStream ins, Charset charset) throws IOException {
        InputStreamReader reader;
        if (charset == null) {
            reader = new InputStreamReader(ins);
        }
        else {
            reader = new InputStreamReader(ins, charset);
        }
        String s = readString(reader);
        reader.close();
        return s;
    }
    
    /**
     * Reads a string from a reader.
     * 
     * @param reader the reader to be read.
     * @return a string read from the specified reader.
     * @throws IOException if there is IO error occurred during the operation.
     */
    public static String readString(Reader reader) throws IOException {
        StringWriter writer = new StringWriter();
        pipe(reader, writer);
        String s = writer.toString();
        writer.close();
        return s;
    }
    
    /**
     * Writes an array of bytes to an output stream.
     * 
     * @param bytes an array of bytes to write.
     * @param out the output stream to be written.
     * @throws IOException if there is IO error occurred during the operation.
     */
    public static void writeBytes(byte[] bytes, OutputStream out) throws IOException {
        ByteArrayInputStream ins = new ByteArrayInputStream(bytes); 
        pipe(ins, out);
        ins.close();
    }
    
    /**
     * Writes an array of bytes to a writer.
     * 
     * @param bytes an array of bytes to write.
     * @param writer to writer to be written.
     * @param charset the charset used to convert the bytes.
     * @throws IOException if there is IO error occurred during the operation.
     */
    public static void writeBytes(byte[] bytes, Writer writer, Charset charset) throws IOException {
        ByteArrayInputStream ins = new ByteArrayInputStream(bytes);
        InputStreamReader reader;
        if (charset == null) {
            reader = new InputStreamReader(ins); 
        }
        else {
            reader = new InputStreamReader(ins, charset);
        }
        pipe(reader, writer);
        reader.close();
        ins.close();
    }
    
    /**
     * Writes a string to an output stream.
     * 
     * @param s the string to write.
     * @param out the output stream to be written.
     * @param charset the charset used to convert the characters.
     * @throws IOException if there is IO error occurred during the operation.
     */
    public static void writeString(String s, OutputStream out, Charset charset) throws IOException {
        OutputStreamWriter writer;
        if (charset == null) {
            writer = new OutputStreamWriter(out);
        }
        else {
            writer = new OutputStreamWriter(out, charset);
        }
        writeString(s, writer);
        writer.close();
    }
    
    /**
     * Writes a string to a writer.
     * 
     * @param s the string to write.
     * @param writer the writer to be written.
     * @throws IOException if there is IO error occurred during the operation.
     */
    public static void writeString(String s, Writer writer) throws IOException {
        StringReader reader = new StringReader(s);
        pipe(reader, writer);
        reader.close();
    }
}