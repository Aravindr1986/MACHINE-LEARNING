package naive_bayes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;


public class Naive_Bayes
{
    Map<String,Integer> spam_wrd_cnt =new HashMap<>();              //spam words
    Map<String,Integer> ham_wrd_cnt =new HashMap<>();               //ham words
    int wrd_cnt_s=0,wrd_cnt_h=0;                                    //total words conts
    int wrd_cnt_u_s=0,wrd_cnt_u_h=0;                                //total unique words
    String stop_words="";
    void get_stop_words(String file_name) throws Exception
    {
            BufferedReader br = new BufferedReader(new FileReader(file_name));
            String line;
            
            while ((line = br.readLine()) != null)
            {
                stop_words +="|"+ line.replaceAll("[^a-zA-Z ]", "");
            }
    }
    int f_count_h=0,f_count_s=0;                                    //total file count for test set                        
    int words_count=0;                                                          
    int count_words(Map word_cnt,String file_name)
    {
        int tot_cnt=0;
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file_name));
            String line;
            String words[];
            while ((line = br.readLine()) != null)
            {
                words = line.replaceAll("[^a-zA-Z ]", "_").split(" ");
                int i=0;
                while(i<words.length)
                {
                   int cnt=0;
                   words[i] =words[i].replaceAll("_", "").trim();
                   if(!words[i].matches("[ ]*"))    //filtering null words
                   {
                       words_count++;
                        if(word_cnt.get(words[i])!= null ) //retrive the word count for increment
                        {
                            cnt=(int) word_cnt.get(words[i]);
                        }
                        else
                        {
                             tot_cnt++;      //incrementing unique word count
                        }   
                        word_cnt.put(words[i],cnt+1);           //incrementing the counter for a word  
                   }
                    i++;
                }
            } 
        }
        catch(Exception e){System.out.println("File Read error: "+e);}        
        return tot_cnt;
    }
  int file_cnt(String path)
  {
      File f=new File(path);
     return(f.listFiles().length);
            
  }
  int read_files(String path,Map m)
    {
            File f=new File(path);
            int count=f.listFiles().length;
            int k=0;
            for (File file : f.listFiles()) 
            {
                if (file.isFile()) 
                {
                   k+= count_words(m,path+"\\"+file.getName());
                }
            }
            return k;
            
    }
  int classify_naive_bayes(File file,int flag)    throws Exception
  {
      int k=0,i=0;
      double prob_h=(double)f_count_h/(double)(f_count_s+f_count_h),prob_s=(double)f_count_s/(double)(f_count_s+f_count_h);
      int val=0;
          if (file.isFile()) 
          {
              BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
              String line;
              String words[];
              while ((line = br.readLine()) != null)
              {
                  i=0;
                  words = line.replaceAll("[^a-zA-Z ]", "_").split(" ");
                  
                  while(i<words.length)
                  {
                         words[i] =words[i].replaceAll("_", "").trim();
                         int loop=1;
                         if(flag==1 && stop_words.indexOf(words[i])>0)
                         {
                             loop=0;
                         }
                         if(!words[i].matches("[ ]*")&& loop==1)    //filtering null words
                         {
                             double x=0;
                             Integer siz=ham_wrd_cnt.get(words[i]);
                             if(siz!=null)
                                 x=siz.doubleValue()+1;
                             else
                                 x=1;
                             prob_h+=Math.log(x/((double)wrd_cnt_h+wrd_cnt_u_h));
                            siz=spam_wrd_cnt.get(words[i]);
                            if(siz!=null)
                               x=siz.doubleValue()+1;
                            else
                               x=1;
                             prob_s+=Math.log(x/((double)wrd_cnt_s+wrd_cnt_u_s));
                         }
                         i++;
                  }
              }
              if(prob_s<prob_h)
                  val=1;
          }
         
      return val;
  }
  int call_naive_bayese(String path,char c,int flag)
  {
      int l=0;
      try
      {
        File f=new File(path);
        for (File file : f.listFiles()) 
        {
            if(c=='s' && classify_naive_bayes(file,flag)==0)
              l+=1;
            else
             if(c=='h')
               l+=classify_naive_bayes(file,flag); 
        }
      }catch(Exception e){System.out.println(e);}
       return l;
  }
    public static void main(String[] args)
    {
        try
        {
            Naive_Bayes nb= new Naive_Bayes();
            String file_loc=args[0];
            nb.get_stop_words(file_loc+"\\stopwords.txt");
            /*calculating priors*/
            nb.f_count_h=nb.file_cnt(file_loc+"\\train\\ham");
            nb.f_count_s=nb.file_cnt(file_loc+"\\train\\spam");  //priors
            nb.wrd_cnt_u_h=nb.read_files(file_loc+"\\train\\ham",nb.ham_wrd_cnt);
            
            nb.wrd_cnt_h=nb.words_count;
            
            nb.words_count=0;
            
            nb.wrd_cnt_u_s=nb.read_files(file_loc+"\\train\\spam",nb.spam_wrd_cnt);
          
            nb.wrd_cnt_s=nb.words_count;
            nb.words_count=0;
            System.out.println("--------------------------------------------------------------");
            int ham_cnt=0,spam_cnt=0;
            int total_count=nb.file_cnt(file_loc+"\\test\\ham");
            ham_cnt=nb.call_naive_bayese(file_loc+"\\test\\ham",'h',0);
            System.out.println("Ham file count:"+ ham_cnt);
            total_count+=nb.file_cnt(file_loc+"\\test\\spam");
            spam_cnt=nb.call_naive_bayese(file_loc+"\\test\\spam",'s',0);
            System.out.println("spam file count:"+ spam_cnt);
            System.out.println("Accuracy with stop words :"+((double)(spam_cnt+ham_cnt)/(double)total_count)*100);
            ham_cnt=nb.call_naive_bayese(file_loc+"\\test\\ham",'h',1);
           // System.out.println("Ham file count:"+ ham_cnt);
            //total_count+=nb.file_cnt(file_loc+"\\test\\spam");
            spam_cnt=nb.call_naive_bayese(file_loc+"\\test\\spam",'s',1);
           // System.out.println("spam file count:"+ spam_cnt);
            System.out.println("Accuracy without stop words :"+((double)(spam_cnt+ham_cnt)/(double)total_count)*100);
            
            }
          catch(Exception e){System.out.println("Error : "+e);}
        
    }
    
}
