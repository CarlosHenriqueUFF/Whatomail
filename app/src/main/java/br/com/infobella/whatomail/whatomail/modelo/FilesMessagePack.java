package br.com.infobella.whatomail.whatomail.modelo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by HENRI on 21/04/2017.
 */

public class FilesMessagePack implements Serializable{

    List<FileMessage> list;

    public FilesMessagePack(List<FileMessage> list) {
        this.list = list;
    }

    public List<FileMessage> getList() {
        return list;
    }

    public void setList(List<FileMessage> list) {
        this.list = list;
    }
}
