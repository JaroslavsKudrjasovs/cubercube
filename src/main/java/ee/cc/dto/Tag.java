package ee.cc.dto;

public class Tag {
    private long id;
    private String name;

    public Tag(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"name\":\"" + name + '\"' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tag))
            return false;
        return id == ((Tag)obj).getId() && name.equals(((Tag)obj).getName());
    }

    public void setName(String name) {
        this.name = name;
    }
}
