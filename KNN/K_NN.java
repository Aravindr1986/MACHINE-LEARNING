package k_nn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

class Records
{
     double record[][];
     String recs[][];
     public Records(int n,int c)
     {
         record=new double[n][c+1];
         recs=new String[n][11];
     }
     int tag(double no)
     {
         int x=0;
         if(no<=10000)
             x=0;
         if(no>10000 && no<=20000)
             x=1;
         if(no>20000 && no<=30000)
             x=2;
         if(no>30000 && no<=40000)
             x=3;
         if(no>40000)
             x=4;
         return x;
     }
     int call_idx(int i)
     {
         switch(i)
         {
             case 2:
             case 3:
             case 4:
             case 5:
             case 6:
             case 7:
             case 8:
             case 14:
             case 15:
             case 17: return(1);        
             default: return(0);
         }
     }
     public void read_Training_records(String csvFile,double record[][])
     {
        String cols[];
        try
        {
            int i=0,k=0;
            BufferedReader br = null;    
            br = new BufferedReader(new FileReader(csvFile));
            String line;
            while ((line = br.readLine()) != null)
            {
                cols=line.split(",");
                int cnts=0;
                for(int j=0;j<26;j++)
                {
                    if(cols[j].equals("?") && call_idx(j)==0)
                        cols[j]=cols[j].replace('?', '0');
                    if(cols[j].matches("([0-9]*)\\.([0-9]*)")||cols[j].matches("([0-9]*)"))       //if the current string contains only digits
                    {
                        record[i][j]=Double.parseDouble(cols[j]);
                    }
                    else                                //else read the lengths
                    {
                        recs[i][cnts]=cols[j]; 
                        cnts++;
                    }
                }   
                i++;    
            }
        }catch(Exception e){System.out.println("File Read error : "+e);}
    }
    SortedMap distance(double test_rec[],String recsa[])
    {
        double sum=0;
        int i=0;
        SortedMap<Double, Integer> sm = new TreeMap<Double, Integer>();
        for(int j=0;j<150;j++)
        {
            sum=0;
            for(i=0;i<25;i++)
            {
                double diff=test_rec[i]-record[j][i];           //distances between each attribute
                sum+=Math.pow(diff, 2);                         
            }
            for(i=0;i<10;i++)
            {
                 if(!recs[j][i].equalsIgnoreCase(recsa[i]))
                 sum+=10;
            }
            sm.put(Math.sqrt(sum), j);
        }
        return sm;
    }
    public static int count_rec(String csvFile)
    {
        int n=0;
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            while ( br.readLine() != null)
                n++;
        }catch(Exception e){System.out.println("Error in count"+e);}
        return n;
    }
   public static int count_col(String fname)
    {
        //int n=0;
        int count=0;
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(fname));
            String line=br.readLine();
            count = line.length() - line.replace(",", "").length();
           // System.out.println(count+1);
            
        }catch(Exception e){System.out.println("Error in count"+e);}
        return count+1;
    }
 }
   
public class K_NN
{
    
    public static void main(String[] args) 
    {
        try
        {
            System.out.println("3-NN algorithim\n--------------------");
            Records r=new Records(Records.count_rec(args[0]),Records.count_col(args[0]));
            r.read_Training_records(args[0], r.record);
            SortedMap<Double, Integer> sm ;
            Records r_test=new Records(Records.count_rec(args[1]),Records.count_col(args[1]));
            r_test.read_Training_records(args[1], r_test.record);
            int correct_cnt=0;
            for(int cnt=0;cnt<r_test.record.length;cnt++)
            {
                final long startTime = System.nanoTime();
                sm=r.distance(r_test.record[cnt],r_test.recs[cnt]);
                Set s=sm.entrySet();
                Iterator i=s.iterator();
                int k=0;
                double sum=0;
                double predicted_value;
                
                r_test.record[cnt][26]=r_test.tag(r_test.record[cnt][25]);  //tagging the record
              //  System.out.println(r_test.record[cnt][25]+" "+r_test.tag(r_test.record[cnt][25])+" "+r_test.record[cnt][26]);
                while(i.hasNext())
                {
                    k++;
                    Map.Entry m = (Map.Entry)i.next();
                    double key = (Double)m.getKey();
                    int value = (Integer)m.getValue(); 
                   // System.out.println("Value :"+value+" "+r.record[value][25]);
                    sum+=r.record[value][25];
                    if(k==3)
                        break;
                }
                predicted_value=(sum/3);
                final long endTime = System.nanoTime();
                if(r_test.record[cnt][26]==r_test.tag(predicted_value))
                 correct_cnt++;
                System.out.println(+startTime+" , "+endTime);
               //System.out.println("class:"+r_test.tag(predicted_value)+" real class:"+r_test.record[cnt][26]);
            }
             System.out.println("Correct_count:"+correct_cnt+"/"+r_test.record.length);
             System.out.println("\n-------------------------------------------");
        }catch(Exception e){System.out.println("Here Error "+e);}
       
    }
    
}
