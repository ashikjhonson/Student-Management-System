import java.sql.*;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

public class StudentManagementSystem extends JFrame implements ActionListener
{
    public static String query="", USER = "", PASS = "";
    static final String URL = "jdbc:mysql://localhost/";
	static Connection conn = null;
    static Statement st = null;
    public static ResultSet rs;

    public static String checkR = null;
    public static JTextField reg, name, m1, m2, m3, check, total, rank;
    public static JButton submit;
    public static JLabel label;


    public StudentManagementSystem()
    {
        super("Student Management System");
        
        JLabel regn = new JLabel("Reg. No.");
        regn.setBounds(30,20,60,30);
        add(regn);
        reg = new JTextField();
        reg.setBounds(100,20,140,30);
        add(reg);

        JLabel namen = new JLabel("Name");
        namen.setBounds(300,20,60,30);
        add(namen);
        name = new JTextField();
        name.setBounds(350, 20, 210, 30);
        add(name);
        
        JLabel m1n = new JLabel("Mark 1");
        m1n.setBounds(75,110,100,20);
        add(m1n);
        m1 = new JTextField();
        m1.setBounds(30,80,130,30);
        add(m1);

        JLabel m2n = new JLabel("Mark 2");
        m2n.setBounds(275,110,100,20);
        add(m2n);
        m2 = new JTextField();
        m2.setBounds(230,80,130,30);
        add(m2);
        
        JLabel m3n = new JLabel("Mark 3");
        m3n.setBounds(475,110,100,20);
        add(m3n);
        m3 = new JTextField();
        m3.setBounds(430,80,130,30);
        add(m3);

        submit = new JButton("Submit");
        submit.setBounds(230,150,130,30);
        submit.addActionListener(this);
        add(submit);

        JLabel checkn = new JLabel("Check");
        checkn.setBounds(170,240,60,25);
        add(checkn);
        check = new JTextField();
        check.setBounds(230,240,130,30);
        add(check);

        JLabel totn = new JLabel("Total Marks");
        totn.setBounds(30,300,70,30);
        add(totn);
        total = new JTextField();
        total.setEditable(false);
        total.setBounds(120,300,130,30);
        add(total);

        JLabel rankn = new JLabel("Rank");
        rankn.setBounds(310,300,40,30);
        add(rankn);
        rank = new JTextField();
        rank.setEditable(false);
        rank.setBounds(350,300,130,30);
        add(rank);

        label = new JLabel("");
        label.setBounds(250,265,130,30);
        add(label);
        
        this.setIconImage(new ImageIcon(getClass().getResource("logo.png")).getImage());
        Color color = new ColorUIResource(153, 200, 255);
        this.getContentPane().setBackground(color);
        setSize(610,420);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void reseter()
    {
        check.setText("");            
        rank.setText("");
        total.setText("");
        label.setText("");
    }

    public static void rankAnalysis() throws SQLException
    {
        int result;
        String rankCheck = check.getText();
        check.setText("");

        st.execute("USE Student");

        query = " select RegisterNumber, Mark1 + Mark2 + Mark3 as total from StudentDetails where RegisterNumber = '" + rankCheck + "';";        
        rs = st.executeQuery(query);
        if(rs.next())
        {
            result =  rs.getInt("total");
            total.setText(Integer.toString(result));
            label.setText(rankCheck.toUpperCase());
        }
        
        query = "SELECT FIND_IN_SET(Mark1+Mark2+Mark3,(SELECT GROUP_CONCAT"
                +"(Mark1+Mark2+Mark3 ORDER BY Mark1+Mark2+Mark3 DESC) FROM "
                +"StudentDetails ) ) AS ranks FROM StudentDetails WHERE RegisterNumber = '"+rankCheck+"';";        
        rs = st.executeQuery(query);
        if(rs.next())
        {
            result =  rs.getInt("ranks");
            rank.setText(Integer.toString(result));
        }
        else
        {
            JOptionPane.showMessageDialog(submit, "Please enter valid Register"+
            " Number\nfor checking details\n\nFormat (VMLXXCSXXX) \nXX - Enter joined year last digits\nXXX - 3 digit Number");
            reseter();
            return;
        }
    }

    public void actionPerformed(ActionEvent e) 
    {
        if(e.getSource() == submit)
        {
            try
            {
                checkR = check.getText();
                if(!checkR.isEmpty())
                {
                    rankAnalysis();
                    return ;
                }
                String regNo = reg.getText();
                String Name = name.getText();
                float mark1 = Float.parseFloat(m1.getText());
                float mark2 = Float.parseFloat(m2.getText());
                float mark3 = Float.parseFloat(m3.getText());
                
                reg.setText("");name.setText("");m1.setText("");m2.setText("");m3.setText(""); // Reseting textfields
                
                mark1 = Math.round(mark1);
                mark2 = Math.round(mark2);
                mark3 = Math.round(mark3);
                reseter();
                st.execute("INSERT INTO StudentDetails VALUES ('"+regNo+"', '"+Name+"', "+mark1+", "+mark2+", "+mark3+")");
            
                JOptionPane.showMessageDialog(submit, "Entered successfuly.");
            }
            catch (SQLIntegrityConstraintViolationException v)
            {
                JOptionPane.showMessageDialog(submit, "Duplicate values found.\nCouldn't update Database.");
            }
            catch (Exception ex)
            {
                reg.setText("");name.setText("");m1.setText("");m2.setText("");m3.setText("");label.setText("");
                JOptionPane.showMessageDialog(submit, "Enter all text fields correctly\nError: "+ex);
            }
        }
    }

    public static void main(String[] args) throws SQLException
    {
        try 
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            USER = "root"; PASS = "MySQL3301";
            // USER = JOptionPane.showInputDialog(null,"user","MySQL user",JOptionPane.QUESTION_MESSAGE);
            // PASS = JOptionPane.showInputDialog(null,"password","MySQL password",JOptionPane.QUESTION_MESSAGE);
            conn = DriverManager.getConnection(URL, USER, PASS);
            st = conn.createStatement();
            
            st.execute("CREATE DATABASE IF NOT EXISTS Student");
            st.execute("USE Student");
            st.execute("CREATE TABLE IF NOT EXISTS StudentDetails(RegisterNumber VARCHAR(11) NOT NULL, Name VARCHAR(25), Mark1 INT NOT NULL, Mark2 INT NOT NULL, Mark3 INT NOT NULL, PRIMARY KEY (RegisterNumber))");
            new StudentManagementSystem();
        } 
        catch (SQLException s)
        {
            JOptionPane.showMessageDialog(null, "You have entered wrong user or password!\nTry once again\nExited Student Management System!!!","MySQL Error",JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        catch (Exception e) 
        {
            System.out.println(e);
        }
    }
}
