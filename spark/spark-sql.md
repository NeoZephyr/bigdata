DataFrame 是 Dataset 的特列，DataFrame=Dataset[Row]，所以可以通过 as 方法将 DataFrame 转换为 Dataset。Row 是一个类型，所有的表结构信息我都用 Row 来表示

临时表是 Session 范围内的，Session 退出后，表就失效了。如果想应用范围内有效，可以使用全局表


export http_proxy=http://127.0.0.1:50236;export https_proxy=http://127.0.0.1:50236;