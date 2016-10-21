package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "rooms")
public class Room extends BaseDaoEnabled{
    public static final String ID_FIELD = "id"; 
    public static final String NAME_FIELD = "name"; 
    
    @DatabaseField(generatedId = true)
    private Long id;
    
    @DatabaseField(canBeNull = false, columnName = NAME_FIELD)
    private String name;
    
    public Room(){
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
