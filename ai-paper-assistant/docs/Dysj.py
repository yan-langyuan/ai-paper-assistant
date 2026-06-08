import pandas as pd
import numpy as np
import random
from datetime import datetime

random.seed(42)
np.random.seed(42)

n = 200

# 基础信息分布（贴合大学生实际）
genders = np.random.choice(['男', '女'], n, p=[0.48, 0.52])
grades = np.random.choice(['大一', '大二', '大三', '大四', '硕士', '博士'], n,
                          p=[0.15, 0.20, 0.35, 0.25, 0.04, 0.01])
majors = np.random.choice(['理工科', '人文社科', '经管类', '医学/农学', '艺术/体育/其他'], n,
                          p=[0.40, 0.25, 0.20, 0.10, 0.05])
ai_usage = np.random.choice(['没用过，想尝试', '偶尔用', '经常用，已融入写作流程', '深度使用'], n,
                            p=[0.20, 0.50, 0.25, 0.05])

# Q1 文献篇数
lit_count_map = {'A': '5篇以内', 'B': '6-15篇', 'C': '16-30篇', 'D': '30篇以上'}
lit_probs = [0.10, 0.45, 0.35, 0.10]
lit_count_letter = np.random.choice(['A','B','C','D'], n, p=lit_probs)
lit_count = [lit_count_map[x] for x in lit_count_letter]

# 多选函数
def multinomial(probs):
    return np.random.choice([0,1], p=[1-probs, probs])

# Q2 (五个选项，每个被选概率)
q2_probs = [0.65, 0.40, 0.70, 0.60, 0.45]  # 筛选耗时、英文、读完就忘、对比归纳、找不到高质量
q2_A = [multinomial(p) for p in [q2_probs[0]]*n]
q2_B = [multinomial(p) for p in [q2_probs[1]]*n]
q2_C = [multinomial(p) for p in [q2_probs[2]]*n]
q2_D = [multinomial(p) for p in [q2_probs[3]]*n]
q2_E = [multinomial(p) for p in [q2_probs[4]]*n]

# Q3 文献管理方式
q3_probs = [0.70, 0.25, 0.20, 0.35, 0.30]  # 文件夹、管理工具、书签、Excel、不管理
q3_A = [multinomial(p) for p in [q3_probs[0]]*n]
q3_B = [multinomial(p) for p in [q3_probs[1]]*n]
q3_C = [multinomial(p) for p in [q3_probs[2]]*n]
q3_D = [multinomial(p) for p in [q3_probs[3]]*n]
q3_E = [multinomial(p) for p in [q3_probs[4]]*n]

# Q4 最需要的AI文献功能
q4_options = ['A','B','C','D','E']
q4_probs = [0.15, 0.55, 0.15, 0.10, 0.05]  # B自动生成摘要最受欢迎
q4_pref = [random.choices(q4_options, weights=q4_probs)[0] for _ in range(n)]

# Q5 开放题（生成简短文本）
q5_texts = [
    "希望AI能自动标注文献中的研究方法部分",
    "自动对比不同文献对同一概念的定义差异",
    "提取文献中的关键公式和参数",
    "自动整理文献中的案例数据",
    "希望AI自动做文献综述的表格",
    "自动提取不同理论流派的对比",
    "希望能自动提取实验数据和结论的关系",
    "自动提取案例公司财务数据",
    "自动标注文献中使用的开源数据集",
    "自动提取小说中的人物关系"
]
q5_text = [random.choice(q5_texts) for _ in range(n)]

# Q6 大纲最大困难
q6_opts = ['A','B','C','D','E']
q6_probs = [0.30, 0.25, 0.20, 0.15, 0.10]
q6_difficulty = [random.choices(q6_opts, weights=q6_probs)[0] for _ in range(n)]

# Q7 最难写的部分
q7_opts = ['A','B','C','D','E']
q7_probs = [0.20, 0.25, 0.25, 0.20, 0.10]
q7_hardest_part = [random.choices(q7_opts, weights=q7_probs)[0] for _ in range(n)]

# Q8 希望AI生成什么
q8_opts = ['A','B','C','D']
q8_probs = [0.30, 0.40, 0.20, 0.10]
q8_pref = [random.choices(q8_opts, weights=q8_probs)[0] for _ in range(n)]

# Q9 排序题（随机生成1-5的排列）
def random_rank():
    ranks = list(range(1,6))
    random.shuffle(ranks)
    return ranks
q9_ranks = [random_rank() for _ in range(n)]
q9_1 = [r[0] for r in q9_ranks]
q9_2 = [r[1] for r in q9_ranks]
q9_3 = [r[2] for r in q9_ranks]
q9_4 = [r[3] for r in q9_ranks]
q9_5 = [r[4] for r in q9_ranks]

# Q10 开放题
q10_texts = [
    "大纲列好了但写作时总跑偏，希望AI能提示章节重点",
    "导师给的框架太抽象，希望AI给个具体例子",
    "数据分析部分最难写，希望AI提供分析框架",
    "不知道从哪章开始写，希望AI推荐顺序",
    "文献综述不知道怎么组织观点",
    "引用了很多书但不知道怎么放进正文",
    "实验设计和数据分析的逻辑链不清晰",
    "SWOT分析不知道怎么组织",
    "实验结果讨论总是不深入",
    "不知道怎么提炼中心论点"
]
q10_text = [random.choice(q10_texts) for _ in range(n)]

# Q11 排版头疼多选
q11_probs = [0.65, 0.40, 0.30, 0.50, 0.25]  # 参考文献、图表目录、页眉页脚、标题样式、公式编号
q11_A = [multinomial(p) for p in [q11_probs[0]]*n]
q11_B = [multinomial(p) for p in [q11_probs[1]]*n]
q11_C = [multinomial(p) for p in [q11_probs[2]]*n]
q11_D = [multinomial(p) for p in [q11_probs[3]]*n]
q11_E = [multinomial(p) for p in [q11_probs[4]]*n]

# Q12 排版时间
q12_opts = ['A','B','C','D']
q12_probs = [0.20, 0.45, 0.25, 0.10]
q12_time = [random.choices(q12_opts, weights=q12_probs)[0] for _ in range(n)]

# Q13 参考文献做法
q13_opts = ['A','B','C','D']
q13_probs = [0.35, 0.45, 0.15, 0.05]
q13_ref_method = [random.choices(q13_opts, weights=q13_probs)[0] for _ in range(n)]

# Q14 最有价值的排版AI功能
q14_opts = ['A','B','C','D']
q14_probs = [0.60, 0.20, 0.10, 0.10]
q14_value = [random.choices(q14_opts, weights=q14_probs)[0] for _ in range(n)]

# Q15 开放题
q15_texts = [
    "参考文献格式不同期刊要求不一样，手动改吐了",
    "论文里很多中文参考文献，知网导出格式总错",
    "图表编号和引用老是对不上",
    "格式调了一晚上页眉还是乱的",
    "知网导出的参考文献总有乱码",
    "英文翻译的引文不知道要不要加原文",
    "公式编号和交叉引用总是错",
    "图表标题和正文对不上",
    "参考文献和正文标号对应不上",
    "手打参考文献格式经常漏点"
]
q15_text = [random.choice(q15_texts) for _ in range(n)]

# Q16 查重态度
q16_opts = ['A','B','C','D']
q16_probs = [0.35, 0.45, 0.15, 0.05]
q16_attitude = [random.choices(q16_opts, weights=q16_probs)[0] for _ in range(n)]

# Q17 降重方式多选
q17_probs = [0.70, 0.55, 0.50, 0.25, 0.10]  # 同义词替换、买查重、自己读、找同学、人工服务
q17_A = [multinomial(p) for p in [q17_probs[0]]*n]
q17_B = [multinomial(p) for p in [q17_probs[1]]*n]
q17_C = [multinomial(p) for p in [q17_probs[2]]*n]
q17_D = [multinomial(p) for p in [q17_probs[3]]*n]
q17_E = [multinomial(p) for p in [q17_probs[4]]*n]

# Q18 降重困扰
q18_opts = ['A','B','C','D','E']
q18_probs = [0.45, 0.25, 0.15, 0.10, 0.05]
q18_trouble = [random.choices(q18_opts, weights=q18_probs)[0] for _ in range(n)]

# Q19 最需要AI降重功能
q19_opts = ['A','B','C','D']
q19_probs = [0.50, 0.30, 0.10, 0.10]
q19_need = [random.choices(q19_opts, weights=q19_probs)[0] for _ in range(n)]

# Q20 开放题
q20_texts = [
    "降重改完意思变了，导师说不行",
    "怕用了降重工具泄露论文",
    "经常为了降重把专业术语改错",
    "改重了10遍还是红",
    "降重后语句不通顺",
    "改重改得自己都看不懂了",
    "降重后案例公司名字变了",
    "降重把引用也改了，导师说不规范",
    "降重把文学性表达改没了"
]
q20_text = [random.choice(q20_texts) for _ in range(n)]

# Q21 PPT准备时间
q21_opts = ['A','B','C','D']
q21_probs = [0.30, 0.40, 0.20, 0.10]
q21_ppt_time = [random.choices(q21_opts, weights=q21_probs)[0] for _ in range(n)]

# Q22 PPT最费精力环节
q22_opts = ['A','B','C','D','E']
q22_probs = [0.40, 0.25, 0.15, 0.10, 0.10]
q22_ppt_effort = [random.choices(q22_opts, weights=q22_probs)[0] for _ in range(n)]

# Q23 最希望AI做
q23_opts = ['A','B','C','D']
q23_probs = [0.45, 0.30, 0.15, 0.10]
q23_ppt_need = [random.choices(q23_opts, weights=q23_probs)[0] for _ in range(n)]

# Q24 演练次数
q24_opts = ['A','B','C','D']
q24_probs = [0.40, 0.35, 0.15, 0.10]
q24_practice_count = [random.choices(q24_opts, weights=q24_probs)[0] for _ in range(n)]

# Q25 开放题
q25_texts = [
    "希望能根据论文内容自动生成图表目录",
    "希望AI能识别哪些是必须保留的核心术语",
    "希望AI能根据论文自动生成讲稿",
    "希望一键生成PPT大纲",
    "希望AI给出降重后的可读性评分",
    "希望AI能保留学术语气",
    "希望AI提供多种改写风格",
    "希望PPT自动配图",
    "希望AI推荐PPT模板"
]
q25_text = [random.choice(q25_texts) for _ in range(n)]

# Q30 补充建议
q30_texts = [
    "希望能导出到Word",
    "手机端操作不方便，希望适配",
    "免费版限制字数少一点",
    "希望能识别手写文献",
    "希望有多语言支持",
    "不要收集个人隐私",
    "希望能离线使用"
]
q30_text = [random.choice(q30_texts) for _ in range(n)]

# 构建DataFrame
df = pd.DataFrame({
    'gender': genders,
    'grade': grades,
    'major': majors,
    'ai_usage': ai_usage,
    'lit_count': lit_count,
    'q2_A': q2_A, 'q2_B': q2_B, 'q2_C': q2_C, 'q2_D': q2_D, 'q2_E': q2_E,
    'q3_A': q3_A, 'q3_B': q3_B, 'q3_C': q3_C, 'q3_D': q3_D, 'q3_E': q3_E,
    'q4_pref': q4_pref,
    'q5_text': q5_text,
    'q6_difficulty': q6_difficulty,
    'q7_hardest_part': q7_hardest_part,
    'q8_pref': q8_pref,
    'q9_1': q9_1, 'q9_2': q9_2, 'q9_3': q9_3, 'q9_4': q9_4, 'q9_5': q9_5,
    'q10_text': q10_text,
    'q11_A': q11_A, 'q11_B': q11_B, 'q11_C': q11_C, 'q11_D': q11_D, 'q11_E': q11_E,
    'q12_time': q12_time,
    'q13_ref_method': q13_ref_method,
    'q14_value': q14_value,
    'q15_text': q15_text,
    'q16_attitude': q16_attitude,
    'q17_A': q17_A, 'q17_B': q17_B, 'q17_C': q17_C, 'q17_D': q17_D, 'q17_E': q17_E,
    'q18_trouble': q18_trouble,
    'q19_need': q19_need,
    'q20_text': q20_text,
    'q21_ppt_time': q21_ppt_time,
    'q22_ppt_effort': q22_ppt_effort,
    'q23_ppt_need': q23_ppt_need,
    'q24_practice_count': q24_practice_count,
    'q25_text': q25_text,
    'q30_text': q30_text
})

# 保存CSV
df.to_csv('ai_paper_assistant_survey_200.csv', index=False, encoding='utf-8-sig')
print("已生成200份模拟问卷数据：ai_paper_assistant_survey_200.csv")