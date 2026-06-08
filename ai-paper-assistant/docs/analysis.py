import pandas as pd
import numpy as np
np.random.seed(42)

df = pd.read_csv('ai_paper_assistant_survey_200.csv')

# ==================== 映射字典 ====================
grade_map = {'大一':'大一', '大二':'大二', '大三':'大三', '大四':'大四', '硕士':'硕士', '博士':'博士'}
q4_map = {'A':'自动推荐相关文献', 'B':'自动生成摘要和核心观点', 'C':'翻译理解外文文献', 'D':'对比多篇文献异同', 'E':'标记可引用段落'}
q6_map = {'A':'整体结构不清晰', 'B':'逻辑关系不顺', 'C':'章节粒度难把握', 'D':'导师框架太模糊', 'E':'写作总是跑偏'}
q14_map = {'A':'一键生成标准参考文献格式', 'B':'检查格式一致性', 'C':'自动生成图表目录和交叉引用', 'D':'一键套用学校模板'}
q18_map = {'A':'改来改去还是红', 'B':'学术表达被改得面目全非', 'C':'不知道哪些改法有效', 'D':'查一次太贵', 'E':'怕工具泄露论文'}
q19_map = {'A':'高亮重复，逐句改写建议', 'B':'保证学术性自动改写', 'C':'分析重复原因分类处理', 'D':'预查重预估重复率'}
q23_map = {'A':'自动提取要点生成PPT大纲', 'B':'一键生成初版PPT', 'C':'生成演讲逐字稿', 'D':'模拟评委提问'}

print('=' * 60)
print('一、用户最迫切需要AI解决的3个论文写作痛点')
print('=' * 60)

# Q2: 文献阅读耗时环节 (多选汇总)
q2_labels = ['筛选判断文献', '英文文献理解', '读完就忘难提取', '观点对比归纳', '找不到高质量文献']
q2_cols = ['q2_A','q2_B','q2_C','q2_D','q2_E']
q2_totals = [df[c].sum() for c in q2_cols]
print('\nQ2 - 文献阅读最耗时环节（多选，n=%d）：' % len(df))
for label, total in sorted(zip(q2_labels, q2_totals), key=lambda x: -x[1]):
    print(f'  {label}: {total}人 ({total/200*100:.1f}%)')

# Q6: 大纲困难
print('\nQ6 - 大纲最大困难：')
q6_counts = df['q6_difficulty'].value_counts()
for k in q6_counts.index:
    print(f'  {q6_map[k]}: {q6_counts[k]}人 ({q6_counts[k]/200*100:.1f}%)')

# Q11: 排版头疼 (多选)
q11_labels = ['参考文献格式', '图表目录生成', '页眉页脚', '标题样式统一', '公式编号引用']
q11_cols = ['q11_A','q11_B','q11_C','q11_D','q11_E']
q11_totals = [df[c].sum() for c in q11_cols]
print('\nQ11 - 排版头疼环节（多选）：')
for label, total in sorted(zip(q11_labels, q11_totals), key=lambda x: -x[1]):
    print(f'  {label}: {total}人 ({total/200*100:.1f}%)')

# Q18: 降重困扰
print('\nQ18 - 降重最大困扰：')
q18_counts = df['q18_trouble'].value_counts()
for k in q18_counts.index:
    print(f'  {q18_map[k]}: {q18_counts[k]}人 ({q18_counts[k]/200*100:.1f}%)')

# 综合痛点排名（基于各维度 TOP1）
print('\n--- 痛点综合小结 ---')
pain_points = {
    '文献："读完就忘、难提取关键信息"': max(q2_totals),
    '大纲："整体结构不清晰、不知从何下手"': q6_counts.get('A',0),
    '排版："参考文献格式问题"': q11_totals[0],
    '降重："改来改去还是红、很绝望"': q18_counts.get('A',0),
}
for name, val in sorted(pain_points.items(), key=lambda x: -x[1]):
    print(f'  {name}: {val}人')

print('\n' + '=' * 60)
print('二、功能需求优先级排名')
print('=' * 60)

# Q4: AI文献功能需求
print('\nQ4 - 最需要的AI文献功能：')
q4_counts = df['q4_pref'].value_counts()
for k in q4_counts.index:
    print(f'  {q4_map[k]}: {q4_counts[k]}人 ({q4_counts[k]/200*100:.1f}%)')

# Q14: 排版AI功能
print('\nQ14 - 最有价值的排版AI功能：')
q14_counts = df['q14_value'].value_counts()
for k in q14_counts.index:
    print(f'  {q14_map[k]}: {q14_counts[k]}人 ({q14_counts[k]/200*100:.1f}%)')

# Q19: 降重AI功能
print('\nQ19 - 最需要的AI降重功能：')
q19_counts = df['q19_need'].value_counts()
for k in q19_counts.index:
    print(f'  {q19_map[k]}: {q19_counts[k]}人 ({q19_counts[k]/200*100:.1f}%)')

# Q23: PPT AI需求
print('\nQ23 - 最需要的答辩PPT AI功能：')
q23_counts = df['q23_ppt_need'].value_counts()
for k in q23_counts.index:
    print(f'  {q23_map[k]}: {q23_counts[k]}人 ({q23_counts[k]/200*100:.1f}%)')

# 综合功能需求排名
print('\n--- 功能需求优先级总排名 ---')
feat_demand = {
    '【P0】文献：自动生成摘要和核心观点提炼': q4_counts.get('B',0),
    '【P0】排版：一键生成标准参考文献格式': q14_counts.get('A',0),
    '【P0】降重：高亮重复段落、逐句改写建议': q19_counts.get('A',0),
    '【P1】PPT：自动提取论文要点生成PPT大纲': q23_counts.get('A',0),
    '【P1】降重：保证学术性的自动改写': q19_counts.get('B',0),
    '【P1】PPT：一键生成初版PPT': q23_counts.get('B',0),
    '【P2】排版：检查全篇格式一致性': q14_counts.get('B',0),
    '【P2】大纲：提供2-3个不同思路供选择': df['q8_pref'].value_counts().get('B',0),
}
for name, val in sorted(feat_demand.items(), key=lambda x: -x[1]):
    print(f'  {name}: {val}人 ({val/200*100:.1f}%)')

print('\n' + '=' * 60)
print('三、不同年级对AI工具的需求差异')
print('=' * 60)

grade_order = ['大一','大二','大三','大四','硕士','博士']
for grade in grade_order:
    gdf = df[df['grade'] == grade]
    n = len(gdf)
    if n == 0:
        continue
    print(f'\n--- {grade}（n={n}）---')
    # AI使用习惯
    ai_counts = gdf['ai_usage'].value_counts()
    ai_str = ', '.join([f'{ai_counts.index[i]}: {ai_counts.values[i]}' for i in range(len(ai_counts))])
    print(f'  AI使用: {ai_str}')

    # Q4 文献AI偏好
    top_q4 = gdf['q4_pref'].value_counts().index[0]
    print(f'  最需文献AI: {q4_map[top_q4]} ({gdf["q4_pref"].value_counts().values[0]})')

    # Q19 降重AI偏好
    top_q19 = gdf['q19_need'].value_counts().index[0]
    print(f'  最需降重AI: {q19_map[top_q19]} ({gdf["q19_need"].value_counts().values[0]})')

    # Q12 排版耗时
    top_q12 = gdf['q12_time'].value_counts().index[0]
    q12_map = {'A':'2h以内','B':'2-5h','C':'5-10h','D':'10h以上'}
    print(f'  排版耗时: {q12_map[top_q12]} ({gdf["q12_time"].value_counts().values[0]})')

# 交叉表：年级 x AI使用习惯
print('\n--- 年级 × AI使用习惯 交叉表 ---')
ct = pd.crosstab(df['grade'], df['ai_usage'])
print(ct.to_string())

print('\n' + '=' * 60)
print('四、理工科 vs 人文社科 需求侧重对比')
print('=' * 60)

for major_group in ['理工科', '人文社科']:
    mdf = df[df['major'] == major_group]
    n = len(mdf)
    print(f'\n--- {major_group}（n={n}）---')

    # Q4 文献AI
    top_q4 = mdf['q4_pref'].value_counts().index[0]
    print(f'  文献AI TOP1: {q4_map[top_q4]} ({mdf["q4_pref"].value_counts().values[0]}, {mdf["q4_pref"].value_counts().values[0]/n*100:.0f}%)')

    # Q19 降重AI
    top_q19 = mdf['q19_need'].value_counts().index[0]
    print(f'  降重AI TOP1: {q19_map[top_q19]} ({mdf["q19_need"].value_counts().values[0]}, {mdf["q19_need"].value_counts().values[0]/n*100:.0f}%)')

    # Q23 PPT AI
    top_q23 = mdf['q23_ppt_need'].value_counts().index[0]
    print(f'  PPT AI TOP1: {q23_map[top_q23]} ({mdf["q23_ppt_need"].value_counts().values[0]}, {mdf["q23_ppt_need"].value_counts().values[0]/n*100:.0f}%)')

    # Q16 查重态度
    attitude_map = {'A':'非常焦虑', 'B':'有些在意', 'C':'不太担心', 'D':'完全不在意'}
    top_q16 = mdf['q16_attitude'].value_counts().index[0]
    print(f'  查重态度: {attitude_map[top_q16]} ({mdf["q16_attitude"].value_counts().values[0]}, {mdf["q16_attitude"].value_counts().values[0]/n*100:.0f}%)')

    # Q12 排版耗时
    q12_map = {'A':'2h以内','B':'2-5h','C':'5-10h','D':'10h以上'}
    q12_dist = mdf['q12_time'].value_counts()
    print(f'  排版耗时分布: ', end='')
    for k in ['A','B','C','D']:
        print(f'{q12_map[k]}:{q12_dist.get(k,0)}人 ', end='')
    print()

print('\n' + '=' * 60)
print('五、用户画像摘要')
print('=' * 60)

print(f'\n总样本量: {len(df)}人')
print(f'\n年级分布:')
for k in grade_order:
    cnt = (df['grade']==k).sum()
    bar = '█' * (cnt // 5)
    print(f'  {k}: {cnt}人 ({cnt/200*100:.1f}%) {bar}')

print(f'\n专业分布:')
for m in ['理工科','人文社科','经管类','医学/农学','艺术/体育/其他']:
    cnt = (df['major']==m).sum()
    bar = '█' * (cnt // 4)
    print(f'  {m}: {cnt}人 ({cnt/200*100:.1f}%) {bar}')

print(f'\n性别分布:')
for g in ['男','女']:
    cnt = (df['gender']==g).sum()
    print(f'  {g}: {cnt}人 ({cnt/200*100:.1f}%)')

print(f'\nAI使用习惯分布:')
for u in ['没用过，想尝试','偶尔用','经常用，已融入写作流程','深度使用']:
    cnt = (df['ai_usage']==u).sum()
    bar = '█' * (cnt // 5)
    print(f'  {u}: {cnt}人 ({cnt/200*100:.1f}%) {bar}')

print(f'\n--- 典型用户画像 ---')
print(f'大三理工科男生，偶尔使用AI工具（如ChatGPT），')
print(f'论文文献以6-30篇为主，最头痛的是读完就忘、')
print(f'参考文献格式和降重改来改去还是红的挫败感。')
print(f'最希望AI能自动生成文献摘要、一键排好参考文献格式、')
print(f'以及提供逐句降重建议。')
