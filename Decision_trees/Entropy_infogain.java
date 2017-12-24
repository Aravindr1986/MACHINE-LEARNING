
//package entropy_infogain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Node implements Cloneable
{
    String idx;
    String Pattern;
    Node zero;
    Node one;
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
public class Entropy_infogain 
{
    String data_set[];
    int n_nleaf = 0;    //denotes number of non leaf nodes
    int tcnt,fcnt;
    String cols[];
    ArrayList<Node> arr= new ArrayList();
    public Entropy_infogain(int size)
    {
        data_set=new String[size];
        n_nleaf=0;
    }
    double count(StringBuilder pattern)
    {
        Pattern p=Pattern.compile(pattern.toString());
         Matcher m;
        double cnt=0;
        for(int i=0;i<data_set.length;i++)
        {
            m=p.matcher(data_set[i]);
            if(m.matches())
                cnt++;
        }
        return cnt;
    }
	
	/*Tree generation with Information gain*/
		
    public double clac_entropy(StringBuilder pattern)
    {
        StringBuilder patt = new StringBuilder(pattern.toString()+"0");
        double neg_cnt=count(patt);//negative count
        patt = new StringBuilder(pattern.toString()+"1");
        double pos_cnt=count(patt);//positive count
        double total_cnt=neg_cnt+pos_cnt;
        double pos=0,neg=0,entrop;
        if(pos_cnt!=0)
        {
            double x=((double)pos_cnt/total_cnt);
            pos=(-1* (double)x*(Math.log(x)/Math.log(2)));
        }
        if(neg_cnt!=0)
        {
            double x=((double)neg_cnt/total_cnt);
            neg=(-1* (double)(x)*(Math.log(x)/Math.log(2)));
        }
        entrop=pos+neg;
        return entrop;
    }
    double clac_infgain(StringBuilder patt,int index)
    {
        StringBuilder pattern = new StringBuilder(patt.toString());
        double overall_entropy = clac_entropy(pattern); //finding the overall entropy
      // System.out.println("Overall Entropy: "+overall_entropy);
        pattern.setCharAt(index, '1');
        double pos_entropy = clac_entropy(pattern);
        pattern.setCharAt(index, '0');
        double neg_entropy = clac_entropy(pattern);
        pattern=new StringBuilder(patt.toString()+".");
        double total_cnt=count(pattern);   //finding the overall count
        pattern.setCharAt(index, '1');
        double pos_cnt=count(pattern);
        pattern.setCharAt(index, '0');
        double neg_cnt=count(pattern);
        //calculate gain
        double gain = overall_entropy -((pos_cnt/total_cnt)*(pos_entropy))-((neg_cnt/total_cnt)*(neg_entropy));
        //System.out.println("Index :"+index+" pos_cnt :"+pos_cnt+" neg_cnt:"+neg_cnt+" pos_entrop:"+pos_entropy+" Neg_entrop:"+neg_entropy+"Gain:"+gain);
        if(gain == 0 )
        {
            
            pattern.setCharAt(index, '.');
            pattern.setCharAt(20,'1');
            StringBuilder pattern1=new StringBuilder(pattern.toString());
            double pcn=count(pattern1);
           // System.out.println("Index : "+index+" Pattern :"+pattern1.toString()+"Gain :"+gain+" pcount:"+pcn);
          /*  pattern.setCharAt(20,'0');
            pattern1=new StringBuilder(pattern.toString());
            double ncn=count(pattern1);*/
            //System.out.println(pattern1.toString()+" ncount:"+ncn);
            if(pcn!=0)
                return -1;
            else
                return gain;
        }
        else
        {
            return gain;
        }
    }
		
     Node build_tree(StringBuilder pattern)
    {
        int i=0;
        double info_g,max_g=-10;
        int maxidx=0;
        for(i=0;i<20;i++)
        {
            if(pattern.charAt(i)=='.')
            {
                
                info_g=clac_infgain(pattern,i);
                
                if(max_g<=info_g)
                {
                    max_g=info_g;
                    maxidx=i;
                }
            }
        }
        //System.out.println("max_gain:"+max_g+"max index:"+maxidx);
        Node n;
        if(max_g==-1)
        {
            //System.out.println("pos here");
            n =new Node();
            n.idx="true";
            n.one=null;
            n.zero=null;
        }
        else
        {
            if(max_g==0)
            {
                n=new Node();
                n.idx="false";
                n.one=null;
                n.zero=null;
            }
            else
            {
                //System.out.println("another call");
                n=new Node();
                n.Pattern=pattern.toString();
                n.idx=""+maxidx;
                n_nleaf+=1;     //incrementing the nonleaf node
                arr.add(n);
                pattern.setCharAt(maxidx, '1');
                n.one=build_tree(new StringBuilder(pattern.toString()));
                pattern.setCharAt(maxidx, '0');
                n.zero=build_tree(new StringBuilder(pattern.toString()));
            }
        }
        
        return n;
    }
	/*Tree Generation with varience impurity*/
    public double clac_VI(StringBuilder pattern)
    {
        StringBuilder patt = new StringBuilder(pattern.toString()+"0");
        double neg_cnt=count(patt);//negative count
       // System.out.println("neg count "/*+patt.toString()*/+" :"+neg_cnt);
        patt = new StringBuilder(pattern.toString()+"1");
        double pos_cnt=count(patt);//positive count
       // System.out.println("pos count "/*+patt.toString()*/+" :"+pos_cnt);
        double total_cnt=neg_cnt+pos_cnt;
        double pos=0,neg=0,entrop;
        if(pos_cnt!=0)
        {
            double x=((double)pos_cnt/total_cnt);
             pos=x;//(-1* (double)x*(Math.log(x)/Math.log(2)));
           // System.out.println("pos :"+pos);
        }
        if(neg_cnt!=0)
        {
            double x=((double)neg_cnt/total_cnt);
            neg=x;//(-1* (double)(x)*(Math.log(x)/Math.log(2)));
         //   System.out.println("neg :"+neg);
        }
        entrop=pos*neg;
        return entrop;
    }
     double clac_vi(StringBuilder patt,int index)
    {
        StringBuilder pattern = new StringBuilder(patt.toString());
        double overall_VI = clac_VI(pattern); //finding the overall entropy
        pattern.setCharAt(index, '1');
        double pos_entropy = clac_VI(pattern);
        pattern.setCharAt(index, '0');
        double neg_entropy = clac_VI(pattern);
        pattern=new StringBuilder(patt.toString()+".");
        double total_cnt=count(pattern);   //finding the overall count
        pattern.setCharAt(index, '1');
        double pos_cnt=count(pattern);
        pattern.setCharAt(index, '0');
        double neg_cnt=count(pattern);
        //calculate gain
       //System.out.println("Final \n pos_cnt :"+pos_cnt+" neg_cnt:"+neg_cnt+" Total count : "+total_cnt+" pos_entrop:"+pos_entropy+" Neg_entrop:"+neg_entropy);
       double gain = overall_VI -((pos_cnt/total_cnt)*(pos_entropy))-((neg_cnt/total_cnt)*(neg_entropy));
       //System.out.println("Gain :"+gain);
       if(gain ==0 )
        {
            pattern.setCharAt(index, '.');
            pattern.setCharAt(20,'1');
            StringBuilder pattern1=new StringBuilder(pattern.toString());
            double pcn=count(pattern1);
            if(pcn!=0)
                return -1;
            else
                return gain;
        }
        else
        {
            return gain;
        }
    }
    Node build_tree_vi(StringBuilder pattern)
    {
        int i=0;
        double info_g,max_g=-10;
        int maxidx=0;
        for(i=0;i<20;i++)
        {
            if(pattern.charAt(i)=='.')
            {
               // System.out.println("Index :"+i);
                info_g=clac_vi(pattern,i);
              //  System.out.println("Info gain:"+info_g);
                if(max_g<=info_g)
                {
                    max_g=info_g;
                    maxidx=i;
                }
            }
        }
      // System.out.println("max_gain:"+max_g+"max index:"+maxidx+"\n---------------------------------------");
        Node n;
        if(max_g==-1)
        {
           
            n =new Node();
            n.idx="true";
            n.one=null;
            n.zero=null;
        }
        else
        {
            if(max_g==0)
            {
              
                n=new Node();
                n.idx="false";
                n.one=null;
                n.zero=null;
            }
            else
            {
                
                n=new Node();
                n.Pattern=pattern.toString();
                n.idx=""+maxidx;
                arr.add(n);
                pattern.setCharAt(maxidx, '1');
                n.one=build_tree_vi(new StringBuilder(pattern.toString()));
                pattern.setCharAt(maxidx, '0');
                n.zero=build_tree_vi(new StringBuilder(pattern.toString()));
            }
        }
        //System.out.println("max_gain:"+max_g+"max index:"+maxidx);
        return n;
    }
    void printPreorder(Node node,int level)
    {
        
        
        if (node == null)
            return;
        if(node.idx.equals("true"))
        {
            System.out.print(" 1");
            return;
        }
        if(node.idx.equals("false"))
        {
            System.out.print(" 0");
            return;
        }
        System.out.println();
        for(int i=0;i<level;i++)
            System.out.print(" ");
        
        System.out.print(cols[Integer.parseInt(node.idx)] + " 1:");
        printPreorder(node.one,level+1);
        System.out.println();
        for(int i=0;i<level;i++)
            System.out.print(" ");
        System.out.print(cols[Integer.parseInt(node.idx)] + " 0:");
        printPreorder(node.zero,level+1);
        
    }
    void printInorder(Node node,int level)
    {
        if (node == null)
            return;
 
        /* first recur on left child */
        //System.out.println("Parent: "+p+"level:"+level+" node:"+ node.idx);
        if(node.idx.equals("true"))
            tcnt++;
        if(node.idx.equals("false"))
            fcnt++;                    
        printInorder(node.one,level+1);
        printInorder(node.zero,level+1);
        //System.out.println();
    }
    void printLevelOrder(Node root)
    {
        int h = height(root);
        int i;
        for (i=1; i<=h; i++)
        {
            System.out.println("\n");
            printGivenLevel(root, i);
        }
    }
      void printGivenLevel (Node root ,int level)
    {
        if (root == null)
            return;
        if (level == 1)
            System.out.print(root.idx + " ");
        else if (level > 1)
        {
            
            printGivenLevel(root.one, level-1);
            printGivenLevel(root.zero, level-1);
        }
    }
    int height(Node root)
    {
        if (root == null)
           return 0;
        else
        {
            /* compute  height of each subtree */
            int lheight = height(root.one);
            int rheight = height(root.zero);
             
            /* use the larger one */
            if (lheight > rheight)
                return(lheight+1);
            else return(rheight+1); 
        }
    }
    int validate_tree(Node root)
    {
        int cnt=0,i=0;
        try
        {
            for( i=0;i<data_set.length;i++)
            {
                
                Node next=(Node)root.clone();
                int k=0;
                while(!next.idx.equals("true") && !next.idx.equals("false"))
                {
            
                   // System.out.println("k="+k+" idx:"+next.idx+" char_at="+data_set[i].charAt(k));
                    int l=Integer.parseInt(next.idx);
                    if(data_set[i].charAt(l)=='1')
                       next=next.one;
                    else
                       next=next.zero;
                    k++;
                }
                if((next.idx.equals("true") && data_set[i].charAt(20)=='1')||(next.idx.equals("false") && data_set[i].charAt(20)=='0'))
                    cnt++; 
                //System.out.println("next.idx: "+next.idx+" chareter: "+data_set[i].charAt(20));
            }
           
        }catch(Exception e){System.out.println("aloha : "+i+" "+e);}
         return cnt;
    }
    
    void read_file(String csvFile)
    {
        try
        {
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(csvFile));
            cols=br.readLine().split(",");
            String line;
            int cnt=0;
            int i=0;
            while ((line = br.readLine()) != null){cnt++;}
            data_set=new String[cnt];
            br = new BufferedReader(new FileReader(csvFile));
           String cols=br.readLine();
           
            while ((line = br.readLine()) != null)
            {
               data_set [i]= line.replace(",","");//.split(cvsSplitBy);
               i+=1;
            }
        }catch(Exception e){System.out.println("File Read error : "+e);}
    }
    Node prune(Node root,int K,int L)
    {
        try
        {
            
            Random no = new Random();
            Node Db=(Node)root.clone();
            Node n_temp=null;
            int limit=arr.size();
            
           /* for(int i=0; i<L;i++)
            {*/
               n_temp=(Node)root.clone();
             
               int m=no.nextInt()%K ;//limiting to leaf node cout
               
               for(int j=0;j<m;j++)
               {
                   int p=no.nextInt(limit);        //randomly selecting node
                 
                   //System.out.println("p="+p);
                   Node start=arr.get(p);           //gstting referecne to node
                 
                   tcnt=0;
                   fcnt=0;
                   printInorder(start,0);           //calculating the true and false counts
                  // System.out.println(" True count:"+tcnt+" false count:"+fcnt);
                   if(tcnt>fcnt)
                      start.idx="true";
                   else
                       start.idx="false";
                   start.zero=null;
                   start.one=null;
               }
               
            //}
            return n_temp;
        }catch(Exception ex){System.out.println("In clone"+ex);}
        return null;
        
    }
    public static void main(String[] args) 
    {
        Entropy_infogain e=new Entropy_infogain(600);
        Entropy_infogain e_vp=new Entropy_infogain(600);
        String csvFile = "training_set.csv";
        
        String line = "";
        String cvsSplitBy = ",";
        int i=0;
        Node root,root_vi;
        StringBuilder pattern = new StringBuilder("....................");
        try 
        {
            int K=Integer.parseInt(args[1]);
            int L=Integer.parseInt(args[0]);
           System.out.println("Information Gain Method");
           System.out.println("--------------------------");
           System.out.println("Training Set : "+args[2] );
           e.read_file(args[2]);
           root=e.build_tree(pattern);      //info gain method
           //i=0;
           if(args[5].equals("yes"))
                 e.printPreorder(root, 0);        //printing the tree
           e.prune(root,K,L);
           System.out.println("\nValidation Set:"+ args[3]);
           e.read_file(args[3]);
           System.out.println("Validate:"+e.validate_tree(root));
           System.out.println("Test Set:"+ args[4]);
           e.read_file(args[4]);
            e.read_file(args[4]);
           System.out.println("Test :"+e.validate_tree(root));
           
           /*Varience Impurity Method*/
           pattern = new StringBuilder("....................");
           System.out.println("Varience Impurity Method");
           System.out.println("--------------------------");
           System.out.println("Training Set : "+args[2] );
           e_vp.read_file(args[2]);
           root_vi=e_vp.build_tree_vi(pattern);      //info gain method
           if(args[5].equals("yes"))
                 e_vp.printPreorder(root_vi, 0);        //printing the tree*/
           e_vp.prune(root_vi,K,L);
           System.out.println("\nValidation Set:"+ args[3]);
           e_vp.read_file(args[3]);
           System.out.println("Validate:"+e_vp.validate_tree(root_vi));
           System.out.println("Test Set:"+ args[4]);
           e_vp.read_file(args[4]);
           System.out.println("Test :"+e_vp.validate_tree(root_vi));
           
        }
        catch(Exception ex){System.out.println("here:"+ex);}
     
    }
    
}
