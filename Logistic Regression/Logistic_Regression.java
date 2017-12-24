
package logistic_regression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Logistic_Regression
{

     ArrayList<String> vocab;
    double weights[];
    double wo;
    double learn_rate,lambda;
    int freq_mat[][];
    int file_cnt;
    public Logistic_Regression(double lr,double lam)
    {
        vocab=new ArrayList<>();
        learn_rate=lr;
        lambda = lam;
        file_cnt=0;
    }
    void generate_Vocab(String loc,boolean stop_words,String stop_loc) throws Exception         //genrating vocabulary
    {
        ArrayList<String> stp_wrds=new ArrayList<>();
        int i=0;
        File file/*=new File(stop_loc)*/;
        String files[]=new String[2];
        String line;
        String words[];
        files[0]=loc+"\\train\\ham";
        files[1]=loc+"\\train\\spam";
        if(stop_words==true)    //reading stop words
        {
            BufferedReader br = new BufferedReader(new FileReader(stop_loc));
            while ((line = br.readLine()) != null)
            {
                String str = line.replaceAll("[^a-zA-Z ]", "");
                str=str.trim().toLowerCase();
                stp_wrds.add(str); 
            }
        }
        i=0;
        while(i<files.length)
        {
            int j=0;
           file=new File(files[i]);
           for (File f : file.listFiles()) 
            {
                BufferedReader br = new BufferedReader(new FileReader(f.getPath()));
                while ((line = br.readLine()) != null)
                {
                   
                   words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split(" ");
                   j=0;
                    while(j<words.length)
                    {
                       words[j]=words[j].trim();
                       if(!words[j].equalsIgnoreCase(""))
                        {
                            if((!stop_words && !vocab.contains(words[j]))||(stop_words && !vocab.contains(words[j]) && !stp_wrds.contains(words[j])))
                            {
                                 vocab.add(words[j]); 
                            }
                        }
                        j++;
                    }
                }
            }   
           i++;
        }
        System.out.println("Vocab size:"+vocab.size());
        weights=new double[vocab.size()];
        for(i=0;i<vocab.size();i++)
        {
            wo=0;
            weights[i]=0;
        }
       freq_mat=new int[500][vocab.size()+1];
    }    
    void calculatefreq(String loc,int cls)      //genration of file wise word frequency
    {
          File f=new File(loc);
          int j;
          try
          {
                int filcnt=file_cnt,word_cnt=0;
                for (File file : f.listFiles()) 
                {
                  BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
                  String line;
                  String words[];
                  while ((line = br.readLine()) != null)
                  {
                    j=0;                                            //since weight[0] is inital weight
                    words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split(" ");
                    while(j<words.length)
                    {
                        words[j]=words[j].trim();
                        if(vocab.contains(words[j]))
                        {
                            word_cnt=vocab.indexOf(words[j]);
                            freq_mat[filcnt][word_cnt]+=1;
                        } 
                        j++;
                    }
                  }
                  
                  freq_mat[filcnt][vocab.size()]=cls;       //recordiing the class  
                  filcnt++;
                
                }
               file_cnt=filcnt; 
          }catch(Exception e){System.out.println("Error :"+e);}
    }
    void calcualte_weights()                                    //running gradiet decent to find weights
    {
        int iterations=1000;
        for(int i=0;i<iterations;i+=1000)
        {
           for (int j=0;j<file_cnt;j++)                       //loop through the entire files
            {
                try
                {
                    double prob=calc_prob(freq_mat[j][vocab.size()],j);
                   wo=(learn_rate*1*(freq_mat[j][vocab.size()]-prob))-(learn_rate*lambda*wo*wo);
                    for(int mi=0;mi<vocab.size();mi+=1)              //weights for every word for this file
                    {
                        int actual_class =freq_mat[j][vocab.size()];                        
                        weights[mi]+=(learn_rate*freq_mat[j][mi]*(actual_class-prob))-(learn_rate*lambda*weights[mi]*weights[mi]);
                    }
                }catch(Exception e){System.out.println(e);}
                
            }
        }
    }
    private double calc_prob(int fclass,int k)                            //probability calculation based on weights.
    {
        double val=wo;
   
        for (int i=0;i<vocab.size();i++)
        {
            val+=weights[i]*freq_mat[k][i];
        }
        double x=1/(1+Math.exp(val));
        double x1=1-x;
        return x1;       
    }
    public int calculate_test_class(String f_name)
    {
        int fcls=0;
        try
        {
            String line;
            String words[];
            Map<String,Integer> w_freq=new HashMap<>();
            int ka=0;
            for(ka=0;ka<vocab.size();ka++)
            {
                w_freq.put(vocab.get(ka),0);
            } 
            BufferedReader br = new BufferedReader(new FileReader(f_name));
           
            while ((line = br.readLine()) != null)
            {
                words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split(" ");
                for(String w :words)
                {
		    if(vocab.contains(w))
                    {
                        w_freq.put(w, w_freq.get(w)+1);
                        
                    }
                }
            }
            // System.out.println("word:"+w_freq.get("subject"));
            double val=0;
            for (int i=0;i<vocab.size();i++)
            {
              
               val+=(weights[i])*(double)w_freq.get(vocab.get(i));
              
            }
          double p=1/(1+Math.exp((double)val));
          double q=1-p;
          if(p<q)
            fcls=1;                
        }
        catch(Exception e){System.out.println("Error in test:"+e);}
        return fcls;
    }
    
    public static void main(String[] args)
    {
        try
        {
            int i=0,fcnt=0,fval=0,count=0;
            String file_loc=args[0];
            double rate=Double.parseDouble(args[1]);
            double lambda=Double.parseDouble(args[2]);
            boolean c=Boolean.parseBoolean(args[3]);
            Logistic_Regression l=new Logistic_Regression(rate,lambda);//0.01,0.00001
            l.generate_Vocab(file_loc, c, file_loc+"\\stopwords.txt");
            l.calculatefreq(file_loc+"\\train\\ham", 1);
            l.calculatefreq(file_loc+"\\train\\spam", 0);
            //System.out.println(" val:"+l.freq_mat[0][l.vocab.size()]);
            l.calcualte_weights();
            String fname[]=new String[2];
            fname[0]=file_loc+"\\test\\spam";
            fname[1]=file_loc+"\\test\\ham";
            while(i<fname.length)
            {
                File f=new File(fname[i]);
                for (File file : f.listFiles()) 
                {
                    int x=l.calculate_test_class(file.getPath());
                    
                    if(x==fval)
                        count++;
                   fcnt++;
                }
               i++;
               fval++;
            }
            double percentage=(double)((double)count/(double)fcnt)*100;
            System.out.println("Accuracy with stop words( "+c+") :"+percentage );
        }catch(Exception e){System.out.println("ERROR:"+e);}
    }

  
    
}
