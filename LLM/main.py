import openai
from openai import OpenAI
from pathlib import Path


def getAllCallChain(path):
    result = []
    with open(path, 'r', encoding='utf-8') as file:
        for line in file:
            result.append(line.strip())
    file.close()
    return result

def questionGenerate(messages, callchain):
    messageAutomatic = {"role": "user",
                "content": "Please provide the location where the " + callchain + " call occurs. "
                           "Just need to reply with the caller code snippets, no process required."}
    messages.append(messageAutomatic)
    return messages


def ruleAgreement1():
    message3 = {"role": "system", "content": "Assuming there are two Java class names A and B, with function MethodA in class A and function MethodB in class B, there are the following rules:"
                                             "1. A.MethodA() or B.MethodB() means calling the MethodA method of class A or calling the MethodB method of class B"
                                             "2. A.MethodA()->B.MethodB() means The MethodA method of class A will call the MethodB method of class B"}
    return message3


def ruleAgreement2():
    message = {"role": "system", "content": "Assuming there is a function call relationship A.MethodA()->B.MethodB()， But in the code, it is in the following form: "
                                            "MethodA() {if (i > 0){MethodB();}}"
                                             "This means that the condition i>0 needs to be met in order to trigger A.MethodA()->B.MethodB()"
                                             "We refer to the conditions that need to be met as constraints"}
    return message

def priorKnowledge(messages):

    # 指定文件夹路径
    folder_path = Path("D:\LLM\CC6Sample")


    # 遍历文件夹中的所有文件
    for file_path in folder_path.iterdir():
        if file_path.is_file():  # 确保是文件
            # 读取文件内容
            with open(file_path, "r", encoding="utf-8") as file:
                content = file.read()
                message = {"role": "system", "content": content}
                messages.append(message)
            file.close()

    return messages

def chatAI(callchain, callerclass):
    client = OpenAI(api_key="API-key", base_url="https://api.deepseek.com/v1")
    identify = {"role": "system", "content": "You are a Java security engineer specializing in researching Java deserialization vulnerabilities"}
    messages = []

    #身份设定
    messages.append(identify)
    # 规则设定
    messages.append(ruleAgreement1())

    messages = priorKnowledge(messages)



    messages = questionGenerate(messages, callchain)


    response = client.chat.completions.create(
    model="deepseek-chat",
    messages= messages,
    stream=False
)

    print(response.choices[0].message.content)

    #第二轮对话
    messageAssistant = {"role": "assistant",
                "content": response.choices[0].message.content}
    messageQuery = {"role": "user",
                "content": "Which fields of the " + callerclass + " class are involved in the above code snippet"
                           "Just need to reply with the answer, no process required."}

    messages.append(messageAssistant)
    messages.append(messageQuery)
    response2 = client.chat.completions.create(
        model="deepseek-chat",
        messages=messages,
        stream=False
    )

    print(response2.choices[0].message.content)
    #
    # #第三轮对话
    messageAssistant2 = {"role": "assistant",
                        "content": response2.choices[0].message.content}
    messageQuery2 = {"role": "user",
                    "content": "What conditions do the fields mentioned above need to meet in order to trigger " + callchain +
                               "When answering questions, please use the following format: "
                                           "Class.field = value or Class.field != value"
                               "Just need to reply with the answer, no process required."}

    messages.append(messageAssistant2)
    messages.append(messageQuery2)

    response3 = client.chat.completions.create(
        model="deepseek-chat",
        messages=messages,
        stream=False
    )

    print(response3.choices[0].message.content)



# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    # chatAI()
    i = 0
    callchains = getAllCallChain("CC6Info/DealResult")

    # callerclass = callchains[1].split("->")[0].split("(")[0].split(".")[-2]
    # chatAI(callchains[1], callerclass)
    while i < len(callchains):
        callerclass = callchains[i].split("->")[0].split("(")[0].split(".")[-2]
        chatAI(callchains[i], callerclass)
        i += 1


