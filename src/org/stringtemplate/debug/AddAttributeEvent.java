package org.stringtemplate.debug;

public class AddAttributeEvent extends ConstructionEvent {
    String name;
    Object value; // unused really; leave for future
    public AddAttributeEvent(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String toString() {
        return "addEvent{" +
            ", name='" + name + '\'' +
            ", value=" + value +
            ", location=" + getFileName()+":"+getLine()+
            '}';
    }
}
