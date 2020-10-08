package lk.sparkx.ncms;

import java.util.HashMap;

/**
 * Created by: thisum
 * Date      : 2020-08-16
 * Time      : 23:18
 **/

public class ObjectRepo
{
    private static ObjectRepo instance;

    private HashMap<String, String> hashMap;

    private ObjectRepo()
    {
        hashMap = new HashMap<String, String>();
    }

    public static ObjectRepo getInstance()
    {
        if(instance == null)
        {
            instance = new ObjectRepo();
        }
        return instance;
    }

    public void addData(String key, String val)
    {
        this.hashMap.put(key, val);
    }

    public void deleteData(String key)
    {
        this.hashMap.remove(key);
    }

    public void updateData(String key, String value)
    {
        this.hashMap.put(key, value);
    }

    public String getData()
    {
        StringBuilder sb = new StringBuilder();
        for(String key: this.hashMap.keySet())
        {
            sb.append(key).append(" : ").append(this.hashMap.get(key));
            sb.append("\n");
        }

        return sb.toString();
    }

}
