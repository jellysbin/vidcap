import json
import math


def get_keypoints(vidname,size):
    posepoints = []
    for i in range(size+1): # 0~num i를 1씩 증가시키면서 반복
        num = format(i,"012") # 0000000000000 문자열로 저장(12자리 0)
        jfilename = vidname +"_"+num +"_keypoints.json"
        with open('/home/js/openpose/output/'+jfilename, 'r') as f:
            json_data = json.load(f)  # json파일 불러오기댐
            #print(json_data['people'][0]['pose_keypoints_2d'])  # 첫번째 사람만 본다. 2명일때 예외처리 나중에해야
            keypoint = {'x': 0, 'y': 0, 'c': 0}  # 마지막 c는 신뢰도..0.3이하면 신뢰하지 않는다
            posepoint = []

            if not json_data['people'] : #openpose의 output은 물체에 사람이 잡히지 않을경우 poeple배열을 비운다. 빈 리스트인지 확인하는 코드
                return posepoints

            for j in range(75):  # 관절개수가 25개(0~24)
                if j % 3 == 0:  # 0번째 자리
                    keypoint['x'] = json_data['people'][0]['pose_keypoints_2d'][j]
                elif j % 3 == 1:
                    keypoint['y'] = json_data['people'][0]['pose_keypoints_2d'][j]
                elif j % 3 == 2:
                    keypoint['c'] = json_data['people'][0]['pose_keypoints_2d'][j]
                    posepoint.append(keypoint.copy())  # 리스트는 깊은복사라서.. copy로
                    # print(keypoint)
        posepoints.append(posepoint.copy())
    return posepoints

def get_slope(x1,y1,x2,y2): #두 점의 좌표를 가지고 기울기를 구하는 함수 (이번 코드에는 사용하지 않았음 ㅎㅎ;)
    if x1 != x2: #분모가 0이되는 상황 방지
        radian = math.arctan((y2-y1)/(x2-x1))
    return radian

def get_angle(joint1,joint2,joint3):#두 몸체의 기울기를 가지고 관절의 각도를구하는 함수      locate ->  j1 ------ j2 ------- j3
    radi1 = math.atan((joint1.get('y')-joint2.get('y'))/(joint1.get('x')-joint2.get('x')))
    radi2 = math.atan((joint3.get('y')-joint2.get('y'))/(joint3.get('x')-joint2.get('x')))
    radian = radi1-radi2
    #radi1 = math.atan((2 - 0) / (0 - joint2.get('y')))
    andgle = radian * (180 / math.pi)
    return abs(andgle) #각도를절댓값으로 변환 ^^

def cut_frame(posepoints) : #프레임을 어깨 각도를 통해 인식
    size = len(posepoints)
    for i in range(size) :
        angle_left_shoulder = get_angle(posepoints[i][1], posepoints[i][2], posepoints[i][3])
        angle_right_shoulder = get_angle(posepoints[i][1], posepoints[i][5], posepoints[i][6])
        if(angle_right_shoulder <= 50) or (angle_left_shoulder <= 50):
            print("어드래스 시작")
        else :
            print("준비~~~")



# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    posepoints = get_keypoints("gfmb1", 71)
    #print(posepoints[0][2]) #순서대로 0프레임의 2번 관절(좌측 어깨) 좌표
    #print(posepoints[0][1])

    #joint num -------
    #1:어깨 중심    2 : 좌측어깨    3:좌측 팔꿈치    5 : 우측어꺠   6:우측팔꿈치
    angle123 = get_angle(posepoints[0][1],posepoints[0][2],posepoints[0][3])
    angle156 = get_angle(posepoints[0][1],posepoints[0][5],posepoints[0][6])
    print(angle156)
    cut_frame(posepoints)


