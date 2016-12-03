package methods;

/**
 * Created by snels on 30.11.2016.
 */
public class HTMLSpecialChars {

    public final String stringToHtmlString(String s){
        StringBuffer sb = new StringBuffer();
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#039;"); break;
                case ';': sb.append("&#059;"); break;
                case '\n': sb.append("<br />"); break;
                default:  sb.append(c); break;
            }
        }
        return sb.toString();
    }
}
