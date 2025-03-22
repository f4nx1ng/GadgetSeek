
def fileread():
    # 定义文件路径
    dir = 'CC6Sample/'
    file_path = dir + "PathResult"
    result_path = dir + "DealResult"

    # 打开文件并读取内容
    with open(file_path, 'r', encoding='utf-8') as file:
        content = file.read()

    # 打印文件内容
    content = content.strip("[")
    content = content.strip("]")
    content = content.split(", ")

    index = 0

    while(index < len(content)):
        item = content[index].strip("<").strip(">").split(" ")
        content[index] = item[0][:-1] + "." + item[2]
        index += 1


    index2 = len(content) - 1
    with open(result_path, 'a', encoding='utf-8') as file:
        while(index2 > 0):
            file.write(content[index2] + "->" + content[index2 - 1] + "\n")
            index2 -= 1
        file.close()



if __name__ == '__main__':
    fileread()