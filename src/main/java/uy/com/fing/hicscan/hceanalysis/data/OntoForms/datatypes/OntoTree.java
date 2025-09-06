package uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class OntoTree {
    private final OntoNode data;
    private final List<OntoTree> children;

    @JsonCreator
    public OntoTree(@JsonProperty("data") OntoNode data, @JsonProperty("children") List<OntoTree> children) {
        this.data = data;
        this.children = children != null ? children : new ArrayList<>();
    }

    public OntoTree(OntoNode rootData) {
        this.data = rootData;
        this.children = new ArrayList<>();
    }

    public OntoTree addChildren(OntoNode data) {
        var e = new OntoTree(data);
        children.add(e);
        return e;
    }

    public record OntoNode(String className, String uri) {}
} 