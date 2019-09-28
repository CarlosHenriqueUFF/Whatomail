package br.com.infobella.whatomail.whatomail.modelo;

/**
 * Created by HENRI on 20/05/2017.
 */

public class Veterinary {

    private Long id;
    private String phone;
    private String name;

    public static final  String TABLE = "Veterinary";

    public static final String ID_SQL_LONG = "_id";
    public static final String NAME_TEXT = "name";
    public static final String PHONE_TEXT = "phone";

    public Veterinary() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJidWhatsApp(){
        String jid = "55" + this.phone +"@s.whatsapp.net";
        return jid;
    }

    @Override
    public String toString() {
        return "Veterinary{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
