## ResourceManager
1. 处理客户端请求：提交作业，终止作业
2. 监控 NodeManager
3. 启动或监控 ApplicationMaster
4. 资源的分配与调度

### 调度器
调度器根据客户端提交的资源申请和当前服务器集群的资源状况进行资源分配。Yarn 内置了几种资源调度算法，包括 Fair Scheduler、 Capacity Scheduler 等。Yarn 进行资源分配的单位是容器

### 应用程序管理器
应用程序管理器负责应用程序的提交、监控应用程序运行状态等。应用程序启动后需要在集群中运行一个 ApplicationMaster


## NodeManager
1. 管理单个节点上的资源
2. 定时向 ResourceManager 汇报节点资源使用情况，处理来自 ResourceManager 的命令
3. 处理来自 ApplicationMaster 的命令


## ApplicationMaster
1. 管理应用程序，为应用程序向 ResourceManager 申请资源
2. 与 NodeManager 通信，启动或停止任务

ApplicationMaster 运行在容器里面。每个应用程序启动后都会先启动自己的 ApplicationMaster，由 ApplicationMaster 根据应用程序的资源需求进一步向 ResourceManager 进程申请容器资源，得到容器以后就会分发自己的应用程序代码到容器上启动，进而开始分布式计算


## Container
容器内包含了一定量的内存、CPU 等 计算资源，默认配置下，每个容器包含一个 CPU 核心。容器由 NodeManager 进程启动和管理，NodeManger 进程会监控本节点上容器的运行状况并向 ResourceManger 进程汇报


## Client
1. 提交作业
2. 终止作业
3. 查询作业进度


## 工作流程
1. 客户端向 Yarn 提交应用程序
2. ResourceManager 进程和 NodeManager 进程通信，根据集群资源，为用户程序分配第一个容器，并将 ApplicationMaster 分发到这个容器上面，并在容器里面启动 ApplicationMaster
3. ApplicationMaster 启动后立即向 ResourceManager 进程注册，并为自己的应用程序申请容器资源
4. ApplicationMaster 申请到需要的容器后，立即和相应的 NodeManager 进程通信，将用户的程序分发到 NodeManager 进程所在服务器，并在容器中运行
5. 任务在运行期和 ApplicationMaster 通信，汇报自己的运行状态，如果运行结束，ApplicationMaster 向 ResourceManager 进程注销并释放所有的容器资源
