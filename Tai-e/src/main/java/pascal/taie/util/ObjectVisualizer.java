package pascal.taie.util;

import pascal.taie.util.graph.SimpleGraph;

import java.io.FileWriter;
import java.io.IOException;

public class ObjectVisualizer {
    /**
     * 将 SimpleGraph 导出为 DOT 文件
     *
     * @param graph    要导出的图
     * @param filePath DOT 文件的保存路径
     */
    public static <N> void exportToDot(SimpleGraph<N> graph, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // 写入 DOT 文件头
            writer.write("digraph G {\n");

            // 遍历所有节点和边
            for (N node : graph.getNodes()) {
                for (N succ : graph.getSuccsOf(node)) {
                    // 写入边
                    writer.write("    \"" + node + "\" -> \"" + succ + "\";\n");
                }
            }

            // 写入 DOT 文件尾
            writer.write("}");
            System.out.println("DOT 文件已生成: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
