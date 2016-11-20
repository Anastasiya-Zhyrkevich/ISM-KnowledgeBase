import java.util.StringTokenizer;

public class Attribute {
    public String name;
    public String value;

    public Attribute() {
        name = new String();
        value = new String();
    }

    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Attribute(String attr) {
        StringTokenizer strTok = new StringTokenizer(attr, "=");
        if(strTok.countTokens() == 2) {
            name = strTok.nextToken().trim();
            value = strTok.nextToken().trim();
        }
        else {
            name = new String();
            value = new String();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Attribute other = (Attribute) obj;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
