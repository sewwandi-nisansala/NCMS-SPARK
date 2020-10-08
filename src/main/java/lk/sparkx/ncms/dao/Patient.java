package lk.sparkx.ncms.dao;

/**
 * Created by: thisum
 * Date      : 2020-09-02
 * Time      : 21:04
 **/

public class Patient
{
    private String name;
    private int age;
    private int x;
    private int y;

    public Patient(String name, int age, int x, int y)
    {
        this.name = name;
        this.age = age;
        this.x = x;
        this.y = y;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }
}
