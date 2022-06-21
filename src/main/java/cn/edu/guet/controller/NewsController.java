package cn.edu.guet.controller;

import cn.edu.guet.mvc.annotation.Controller;
import cn.edu.guet.mvc.annotation.RequestMapping;
import cn.edu.guet.util.WangEditor;
import com.google.gson.Gson;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

@Controller
public class NewsController {
    /*
    一个负责文件上传
    一个负责接收文字内容信息
     */
    @RequestMapping("/upload")
    public void upload(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        int index = uri.lastIndexOf("/");
        uri = uri.substring(index + 1);
        Gson gson = new Gson();
        String realPath = request.getServletContext().getRealPath("/upload");
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);// 检查输入请求是否为multipart表单数据。
        if (isMultipart == true) {
            FileItemFactory factory = new DiskFileItemFactory();// 为该请求创建一个DiskFileItemFactory对象，通过它来解析请求。执行解析后，所有的表单项目都保存在一个List中。
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> items = null;
            try {
                items = upload.parseRequest(request);
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
            Iterator<FileItem> itr = items.iterator();
            // String filePath=System.getProperty("user.dir")+ File.separator;
            while (itr.hasNext()) {
                FileItem item = (FileItem) itr.next();
                // 检查当前项目是普通表单项目还是上传文件。
                if (item.isFormField()) {// 如果是普通表单项目，显示表单内容。
                    String fieldName = item.getFieldName();
                    String value = item.getString();
                    if (fieldName.equals("username")) {
                    } else if (fieldName.equals("password")) {
                    }
                } else {// 如果是上传文件，显示文件名。
                    File fullFile = new File(item.getName());
                    File savedFile = new File(realPath + "/", fullFile.getName());
                    try {
                        item.write(savedFile);// 真正的把文件上传到服务器

                        response.setContentType("application/json;utf-8");
                        PrintWriter out = response.getWriter();
                        String url = "upload/" + fullFile.getName();
                        String[] strs = {url};
                        WangEditor editor = new WangEditor(strs);
                        out.print(gson.toJson(editor));
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.print("the enctype must be multipart/form-data");
        }
    }
    @RequestMapping("/news")
    public void news(HttpServletRequest request){
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String newsContent = request.getParameter("mytxtIntro");
        System.out.println("富文本内容：" + newsContent);
    }
}
