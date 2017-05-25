#shell 脚本总结
ifconfig |grep addr:|awk -F ':' 'NR == 3{print $2}'| awk '{print $1}'

#不能截取 空格
cut -d " " -f 5

#awk是一个比较复杂的命令
#awk ‘条件一{动作一} 条件二{动作二}’ 文件名

df -h |awk '{print $1 "\t" $5}'

#awk 后面的动作可以加格式控制符如上

#可以加条件控制  截取字符串  动作中和cut类似
df -h |grep sda1|awk '{print $5}'|awk -F '%' '{print $1}'

#grep -v  取反
#添加判断条件
cat test1.txt |grep -v Name | awk '$6 >= 100 {print $2}'

#sed 进行选取 替换 删除 增加
#打印对应的行数
sed -n  
#允许对输入数据进行多条sed 命令
sed -e
#直接修改文件
sed -i
#替换
sed "s/开始/结束/g"

#find命令
#5分钟内修改文件属性
find /etc -cmin -5  
#-cmin  更改
#-amin  访问
#-mmin  修改
#-name -iname不区分大小写
#-a  and
#-o  or
#eg：

find /etc -size +163840 -a  -size -24680


find /etc -name initab -exec ls -l {} \;

#文件类型 -type f（文件） d（目录） l (连接) 


find /etc -name init* -a -type f exec ls -l {} \;

# 查看i结点 
ls -i
#通过i结点删除一个文件
find . -inum 2313  -exec rm {} \;


  
