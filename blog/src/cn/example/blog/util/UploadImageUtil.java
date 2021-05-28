package cn.example.blog.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UploadImageUtil {
    private static final long serialVersionUID = 1L;
    // 上传文件存储目录
    private static final String UPLOAD_DIRECTORY = "upload";
    // 上传配置
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

    /**
     * 上传数据及保存文件
     */
    public static void upload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        // 检测是否为多媒体上传
        if (!ServletFileUpload.isMultipartContent(request)) {
            // 如果不是则停止
            PrintWriter writer = response.getWriter();
            writer.println("Error: 表单必须包含 enctype=multipart/form-data");
            writer.flush();
            return;
        }

        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时存储目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);
        // 设置最大文件上传值
        upload.setFileSizeMax(MAX_FILE_SIZE);
        // 设置最大请求值 (包含文件和表单数据)
        upload.setSizeMax(MAX_REQUEST_SIZE);
        // 中文处理
        upload.setHeaderEncoding("UTF-8");
        // 构造临时路径来存储上传的文件
        // 这个路径相对当前应用的目录
        String uploadPath = request.getServletContext().getRealPath("/") + File.separator + UPLOAD_DIRECTORY;
        // 如果目录不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        try {
            // 解析请求的内容提取文件数据
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);

            if (formItems != null && formItems.size() > 0) {
                // 迭代表单数据
                for (FileItem item : formItems) {
                    // 处理不在表单中的字段
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        //将文件名切分成数组
                        String[] splitFileName = fileName.split("\\.");
                        //检测图片文件类型
                        String fileTyep = splitFileName[splitFileName.length - 1].trim();
                        if (!fileTyep.equals("jpeg") && !fileTyep.equals("jpg")
                                && !fileTyep.equals("png") && !fileTyep.equals("gif")) {
                            JSONObject msg = new JSONObject();
                            msg.put("msg", "只支持jpeg jpg png gif图片");
                            response.getWriter().print(msg);
                            return;
                        }
                        String filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
                        //修改文件名
                        int rand = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;//产生1000-9999的随机数
                        //获取当前时间
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        String dateStr = format.format(new Date());
                        String newFileName = dateStr + rand + "." + fileTyep;
                        String newFilePath = uploadPath + File.separator + newFileName;
                        //storeFile.renameTo();
                        // 在控制台输出文件的上传路径
                        //System.out.println(filePath);
                        // 保存文件到硬盘
                        item.write(storeFile);
                        storeFile.renameTo(new File(newFilePath));
                       /* request.setAttribute("message",
                                "文件上传成功!");*/
                        JSONObject msg = new JSONObject();
                        msg.put("msg", "ok");
                        msg.put("imgurl", "http://" + request.getServerName() + "/upload/" + newFileName);
                        response.getWriter().print(msg);
                    }
                }
            }
        } catch (Exception ex) {
          /*  request.setAttribute("message",
                    "错误信息: " + ex.getMessage());*/
            JSONObject msg = new JSONObject();
            msg.put("msg", "上传失败" + ex.getMessage());
            msg.put("imgurl", "");
            response.getWriter().print(msg);
        }
        // 跳转到 message.jsp
       /* request.getServletContext().getRequestDispatcher("/message.jsp").forward(
                request, response);*/
    }
}