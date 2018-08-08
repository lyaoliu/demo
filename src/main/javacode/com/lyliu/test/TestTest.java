package com.lyliu.test;

import com.lyliu.common.dao.PageInfo;
import com.lyliu.common.service.CommonService;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ${name} class
 *
 * @author lyliu
 * @date 2018/05/03 下午 6:11
 */
public class TestTest {
  //@Test
    public void Test(){
      ApplicationContext applicationContext=new ClassPathXmlApplicationContext("applicationContext.xml");
      System.out.println(applicationContext.getApplicationName());
  }
    @Test
    public void test1() {
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("config/applicationContext.xml");
        CommonService service= (CommonService) applicationContext.getBean("commonService");
         PageInfo list=service.listUser(null);
        System.out.println(list);

         List list1=list.getResult();
         for(int i=0;i<list1.size();i++){
             System.out.println(list1.get(i)+"--"+i);

         }
        System.out.println(applicationContext.getApplicationName()+"1111");
    }
   @Test
    public void test2(){
       Connection connection=null;
       ResourceBundle resource = ResourceBundle.getBundle("config/jdbc");
       String user=resource.getString("jdbc.username");
       String password=resource.getString("jdbc.password");
       String url=resource.getString("jdbc.url");
       String  driverClassName=resource.getString("jdbc.driverClassName");
       try {
           Class.forName(driverClassName);
          connection= DriverManager.getConnection(url,user,password);
           Statement stmt = connection.createStatement() ;
           //静态sql
          stmt.execute("select * from tauser where loginid=\'developer\'");
           ResultSet resultSet=stmt.getResultSet();
           while (resultSet.next()){
               System.out.println(resultSet.getString("name"));
           }
           //动态sql
           String sql= "select * from tauser where userid=?";
           PreparedStatement pstmt = connection.prepareStatement(sql) ;
           pstmt.setString(1,"1");
           resultSet=pstmt.executeQuery();
           while (resultSet.next()){
               System.out.println(resultSet.getString("name"));
           }
           stmt.close();
           pstmt.close();
           connection.close();
       } catch (ClassNotFoundException e) {
           e.printStackTrace();
       } catch (SQLException e) {
           e.printStackTrace();
       }

   }

   @Test
    public void test3(){
        String url="http://qixin.flagsky.com/api/SmSendServer";
        String account="test";
        String pwd="test";
        String mobile="18602895867";
        String msg="黄石大冶大联动短信测试";
       CloseableHttpClient client= HttpClients.createDefault();
       HttpPost post=new HttpPost(url);
       // 添加所需要的post内容
       try {
       List<NameValuePair> formparams = new ArrayList<NameValuePair>();
       formparams.add(new BasicNameValuePair("account",URLEncoder.encode(account,"utf-8")));
       formparams.add(new BasicNameValuePair("pwd",URLEncoder.encode(pwd,"utf-8")));
       formparams.add(new BasicNameValuePair("mobile",mobile));
       formparams.add(new BasicNameValuePair("msg",URLEncoder.encode(msg,"UTF-8")));
       post.setEntity(new UrlEncodedFormEntity(formparams,Consts.UTF_8));
          HttpResponse response= client.execute(post);
           if(response.getStatusLine().getStatusCode()==200){
               String result=EntityUtils.toString(response.getEntity());
               System.out.println(result);
           }
       } catch (IOException e) {
           e.printStackTrace();
       }

   }
   @Test
   public void test4(){
       String url="";
       String account="test";
       String pwd="test";
       String mobile="18602895867";
       String msg="黄石大冶大联动短信测试";
       try {
           URL url1=new URL(url);
           URLConnection connection= url1.openConnection();
           // 设置通用的请求属性
           connection.setRequestProperty("accept", "*/*");
           connection.setRequestProperty("connection", "Keep-Alive");
           connection.setRequestProperty("user-agent",
                   "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
           // 发送POST请求必须设置如下两行
           connection.setDoInput(true);
           connection.setDoOutput(true);
           try(PrintWriter out=new PrintWriter(connection.getOutputStream())){
               // 发送请求参数
               StringBuilder stringBuilder=new StringBuilder("account=").append(account);
               stringBuilder.append(account).append("&pwd=").append(pwd).append("&mobile=")
                       .append(mobile).append("&msg=").append(URLEncoder.encode(msg,"UTF-8"));

               //// flush输出流的缓冲
               out.print(stringBuilder.toString());
               out.flush();
           }catch (Exception e){
               e.printStackTrace();
           }

           try(BufferedReader in= new BufferedReader(new InputStreamReader(connection.getInputStream()))){
               String line;
               StringBuilder stringBuilder=new StringBuilder();
               while ((line=in.readLine())!=null){
                   stringBuilder.append(line);
               }
               System.out.println(line);
           }catch (Exception e){
               e.printStackTrace();
           }
       } catch (MalformedURLException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
   @Test
    public void test(){
       Map<String, String> map = new HashMap<String, String>(1024);
       for (int i = 0; i < 768; i++) {
           map.put("key"+i, "value"+i);
       }
        String b="a";
       String c="ab";
       System.out.println(c.hashCode());
       System.out.println(b.hashCode());
       Long T1 =  System.nanoTime();
       map.put("1", "1");
       Long T2 =  System.nanoTime();
       map.put("2", "2");
       Long T3 =  System.nanoTime();
       System.out.println("发生扩容耗时： "+(T2-T1));//发生扩容耗时： 198666
       System.out.println("未发生扩容耗时："+(T3-T2));//未发生扩容耗时：5333
   }
   @Test
  public void testThread(){
       ThreadPoolExecutor poolExecutor=new ThreadPoolExecutor(10,15,10,TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(10)) ;
       Class cl= null ;
       try {
         cl= Class.forName("TestTest");
       } catch (ClassNotFoundException e) {
           e.printStackTrace();
       }
       System.out.println(cl);

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("开始执行");
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("执行完毕");
            }
        };
        for(int i=0;i<100;i++){
            poolExecutor.execute(runnable);
            System.out.println("线程池中线程数目："+poolExecutor.getPoolSize()+"，队列中等待执行的任务数目："+
                    poolExecutor.getQueue().size()+"，已执行玩别的任务数目："+poolExecutor.getCompletedTaskCount());
        }
        poolExecutor.shutdown();

  }

    public static void main(String[] args) {
        try {
            Class  aClass= ClassLoader.getSystemClassLoader().loadClass("com.lyliu.test.TestTest");
            System.out.println(ClassLoader.getSystemClassLoader().loadClass("com.lyliu.test.TestTest"));
            System.out.println(ClassLoader.getSystemClassLoader().getParent());
            System.out.println(ClassLoader.getSystemClassLoader().getParent().getParent());
           /* Class C=Class.forName("testTest");
            System.out.println(C);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
