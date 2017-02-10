package hk.hku.cecid.edi.as2.pkg;

/**
 * DispositionException is a runtime exception representing an error-level 
 * disposition.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class DispositionException extends RuntimeException {

    private Disposition disposition;

    private String text;

    public DispositionException(Disposition disposition, String text,
            Throwable cause) {
        super(disposition.toString(), cause);
        this.disposition = disposition;
        this.text = text;
    }

    public DispositionException(Disposition disposition, String text) {
        this(disposition, text, null);
    }

    public Disposition getDisposition() {
        return disposition;
    }

    public void setDisposition(Disposition disposition) {
        this.disposition = disposition;
    }

    public String getText() {
        return text;
    }

    public void setText(String string) {
        text = string;
    }
}