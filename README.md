# YouTogether

#### 친구들과 유튜브 같이보기 

<br/>

## Members


|<a href="https://github.com/ghkdgus29"><img src = "https://avatars.githubusercontent.com/u/91525492?v=4" width="120px;">|<a href="https://github.com/yeonise"><img src = "https://avatars.githubusercontent.com/u/105152276?v=4" width="120px;">|<a href="https://github.com/poco111"><img src = "https://avatars.githubusercontent.com/u/101160636?v=4" width="120px;">|
|:---:|:---:|:---:|
|[Hyun](https://github.com/ghkdgus29)|[Fia](https://github.com/yeonise)|[Poco](https://github.com/poco111)|

<br/>

<br/>

## Environment

<img src="https://img.shields.io/badge/Language-%23121011?style=for-the-badge"><img src="https://img.shields.io/badge/java-%23ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"><img src="https://img.shields.io/badge/17-515151?style=for-the-badge">

<img src="https://img.shields.io/badge/Framework-%23121011?style=for-the-badge"><img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"><img src="https://img.shields.io/badge/3.2.0-515151?style=for-the-badge">

<img src="https://img.shields.io/badge/Build-%23121011?style=for-the-badge"><img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white"><img src="https://img.shields.io/badge/8.5-515151?style=for-the-badge">

![Nginx](https://img.shields.io/badge/nginx-65B741?style=for-the-badge&logo=nginx&logoColor=white)
![redis](https://img.shields.io/badge/redis_stack-B31312?style=for-the-badge&logo=redis&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-%23121011.svg?style=for-the-badge&logo=githubactions&logoColor=white)

<br/>

## 데이터 모델
![image](https://github.com/mujik-tigers/you-together/assets/91525492/6ef93caa-4f5b-435d-a1c9-d61a787ec712)

<br/>

## 유저 역할에 따른 권한표
호스트가 가장 높은 권한을 가지고 있고, 뷰어는 가장 낮은 권한을 가집니다. <br/>
특정 역할이 가진 권한은 특정 역할의 상위 역할들도 행사할 수 있습니다.

- 호스트
  - 방 생성 시, 기본으로 주어지는 등급으로 방 제목을 변경할 수 있습니다. 
- 매니저
  - 다른 유저의 권한을 변경할 수 있습니다. 
- 에디터
  - 영상 추가, 삭제, 싱크 변경과 같은 유튜브 영상 조작을 할 수 있습니다.
- 게스트
  - 방 입장 시 기본으로 주어지는 등급입니다.
  - 채팅을 할 수 있습니다.
- 뷰어
  - 채팅을 칠 수 없습니다. 

## Demo

1. 방을 생성할 수 있습니다.
![1](https://github.com/mujik-tigers/you-together/assets/91525492/0dbff660-e93b-4430-b61c-6b8d52a7b6a7)

<br/>

2. 방 목록을 조회할 수 있습니다.
![2](https://github.com/mujik-tigers/you-together/assets/91525492/73b63a6a-01df-4f08-9724-bce3a5cc68d5)

<br/>

3. 방에 입장할 수 있습니다.
![3](https://github.com/mujik-tigers/you-together/assets/91525492/61ee8f1b-9eff-496b-aed7-ea4146cafe67)

<br/>

4. 비밀번호를 입력하여 비밀방에 입장할 수 있습니다.
![4](https://github.com/mujik-tigers/you-together/assets/91525492/9d47261d-be82-42eb-bfbf-5f4a7b14773a)

<br/>

5. 방 안의 구성원들은 자유롭게 채팅을 할 수 있습니다.
![5](https://github.com/mujik-tigers/you-together/assets/91525492/21eb42fc-c27d-427c-b293-095b7363fe9d)

<br/>

6. 사용자의 역할을 변경할 수 있습니다.
![6-4](https://github.com/mujik-tigers/you-together/assets/91525492/3a1fa84e-db22-418a-b000-a81a8be7df20)

<br/>

7. 재생목록에 영상을 추가하거나 삭제할 수 있습니다.
![7-3](https://github.com/mujik-tigers/you-together/assets/91525492/998b0fc4-c951-4a42-b82e-c77f6970d38a)

<br/>

8. 호스트 역할의 사용자는 방 제목을 변경할 수 있습니다.
![8-3](https://github.com/mujik-tigers/you-together/assets/91525492/01dde580-b861-4c2a-886c-c43c4ac762ce)

<br/>

9. 자신의 닉네임을 변경할 수 있습니다.
![9-3](https://github.com/mujik-tigers/you-together/assets/91525492/b1809e88-76cf-48d2-84da-9c4f9a9232f7)

<br/>

10. 방 구성원들은 유튜브 싱크를 맞춰 같이 볼 수 있습니다.
![10-6 (1)](https://github.com/mujik-tigers/you-together/assets/91525492/d70b50ac-b6df-483e-a1df-d0b7b29cfe29)

<br/>

## 참고 링크

### [YouTogether 사이트](https://you-together.site/)
### [API 명세서](https://api.you-together.site/docs/index.html)
