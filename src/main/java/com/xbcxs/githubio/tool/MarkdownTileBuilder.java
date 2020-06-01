package com.xbcxs.githubio.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据blog目录机构生成对应Markdown标题结构
 */
public class MarkdownTileBuilder {

    private Logger log = LoggerFactory.getLogger(MarkdownTileBuilder.class);

    /**
     * 要解析的文件跟目录
     */
    private String blogDirName;
    /**
     * 要写入的跟文件
     */
    private String readmeName;

    public static void main(String[] args) throws Exception {
        MarkdownTileBuilder mtb = new MarkdownTileBuilder("blog", "readme.md");
        mtb.build();
    }

    public MarkdownTileBuilder(String blogDirName, String readmeName) {
        this.blogDirName = blogDirName;
        this.readmeName = readmeName;
    }

    /**
     * 构建readme.md
     *
     * @throws IOException
     */
    private void build() throws IOException {
        String projectPath = new ClassPathResource("").getFile().getParentFile().getParent();
        String readmePath = projectPath + File.separator + readmeName;
        String blogPath = projectPath + File.separator + blogDirName;
        generateReadme(readmePath, generateTitle(new File(blogPath)));
    }

    /**
     * 根据文件目录结构生成对应MarkDown标题结构
     *
     * @param blogFile
     * @throws UnsupportedEncodingException
     */
    private List<String> generateTitle(File blogFile) throws UnsupportedEncodingException {
        List blogLists = new ArrayList<String>();
        recursiveFile(blogFile, 0, blogLists);
        return blogLists;
    }

    /**
     * 文件递归
     *
     * @param file
     * @param level
     * @param blogLists
     * @throws UnsupportedEncodingException
     */
    private void recursiveFile(File file, int level, List blogLists) throws UnsupportedEncodingException {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                String relativeFilePath = f.getPath().substring(f.getPath().indexOf(blogDirName));
                relativeFilePath = URLEncoder.encode(relativeFilePath.replace(File.separator, "/"), "UTF-8");
                blogLists.add(formatTitle(level, f.getName(), f.isFile(), relativeFilePath));
                if (f.isDirectory()) {
                    recursiveFile(f, level + 1, blogLists);
                }
            }
        }
    }

    /**
     * 格式化标题
     *
     * @param level
     * @param fileName
     * @param isFile
     * @param relativeFilePath
     * @return
     */
    private String formatTitle(int level, String fileName, boolean isFile, String relativeFilePath) {
        return getIndent(level) + "- [" + fileName + "](" + "https://github.com/xbcxs/xbcxs.github.io/" + (isFile ? "blob" : "tree") + "/master/" + relativeFilePath + ")";
    }

    /**
     * 缩进
     *
     * @param level
     * @return
     */
    private String getIndent(int level) {
        String titleSymbol = "";
        for (int i = 0; i < level; i++) {
            titleSymbol = titleSymbol + "  ";
        }
        return titleSymbol;
    }

    /**
     * 将contentList内容写入filePath对应的文件
     *
     * @param filePath
     * @param contentList
     */
    private void generateReadme(String filePath, List<String> contentList) {
        FileOutputStream fos;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(new File(filePath));
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            for (String content : contentList) {
                bw.write(content);
                bw.newLine();
                log.debug("写入内容:{}", content);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
