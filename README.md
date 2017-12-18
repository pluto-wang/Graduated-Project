# Graduated Project  QnA Session Helper

## Abstract
During a Q-and-A session of a conference organized in a large meeting venue,it takes much time for the session chair to wait conference attendants moving around and delivering a handheld microphone to a remote questioner. In this project, we propose QnA Session Helper as a total solution to solve this problem. With the pre-installations of the server application on the host computer and the client App on
each of the handheld devices of the conference attendees, an efficient and well-organized Q-and-A session providing all attendees with their personal microphones can be achieved.This system is consisted of three modules,and this paper mainly illustrates two modules separately,namely the seat arrangememt and the session control.

## Introducation
- Motivation <br/>
會議是人類社會的一種社交、公關、政治、意見交流、訊息傳播及溝通的活動，有鑑於當今會議環境的多元性，會議的流暢度有時會受到會議環境的影響，
尤其是大型的會議參與人數眾多，若採用互動問答的方式進行，會議流程的拿捏更顯重要，為了讓所有出席者清楚聽到發言者所說得話，常使用麥克風及擴音器
等設備來輔助，但礙於成本的關係，很難達到人手一支麥可風的要求，在這樣的會議環境之下，勢必要浪費些許時間來傳遞麥克風，而本專題便是針對此種會議
環境所設計，希望能解決此問題進而提升會議流暢度。

- Objective <br/>
本專題特別針對大型會議環境，欲設計一項工具來解決問答式發言的問題，並提升會議進行的流暢度，透過來賓的手持裝置取代麥克風的功能，此外，提供
主持人審核發言的權利，運用事先建置好的會議座位表，讓主持人了解當前發言人所在位置以及提出發言申請人的位置，更能掌控會議流程及來賓狀況。
來賓取得發言權後即可透過手持裝置將聲音傳送到主控台，再藉由主控台之喇叭播放，省去傳遞麥克風之等待時間，主持人亦可控制來賓發言的時間，以免
來賓發言過於冗長影響會議流程。

## System Architecture

本系統的架構部分主要分成兩個部分，Server 端及Client 端，其中Server端為主持人的主控台，Client 端為來賓的手持裝置，Server 端包含三個步驟而
Client 端包含四個步驟，我們將這七個步驟組合成三個Module。 <br/>

- Module 1 <br/>
Server 端需先建置議場座位表，再傳送至Client 端，Client 端接收到座位表後，來賓再將自己所在位置設置完成，之後傳回Server 端，
而議場座位表也會即時更新，主持人能清楚知道來賓所在位置。<br/> 

- Module 2 <br/>
當來賓想發言時，需提出發言申請並等待Server 端的許可，Server 端接收到Client 端的申請後，可以選擇給予申請者發言權或拒絕給予，
當Client 端得到發言權後，來賓即可發言。 <br/>

- Module 3 <br/> 
Server 端有權力控制發言人的發言時間，若發言人的發言過於冗長，占用過多會議時間，主持人可以直接中斷其發言，將其發言權取回，
當Client 端沒有發言權後便無法再發言 <br/>

## System Implementtaion 
Server 端為以 java 撰寫之應用程式，而 Client 端則為 android 開發之 App <br/>

- 音頻部分運用 UDP 來傳送 <br/>
- Server 端建置座位表的方法有兩種，第一種為直接以滑鼠點擊來刻畫出單一位置，第二種為 mesh 的方式，輸入行數及列數來建置大量座位，建完議場座位
表後，再將位置之X 軸及Y 軸座標傳送至 Client 端，Client 端依接收到的座標建立出座位表<br/>
- 當 Client 端想發言時，需對 Server 端提出申請，Server 端接收到 Client 端之申請後，可允許或拒絕給予發言權，再將回應傳送回 Client 端，Client 端接
收回應後，若回應為允許則取得發言權並得以發言，若為拒絕則無法發言 <br/>
- 座位表會即時的更新每個座位的狀態 (正在發言，提出申請，未發言) <br/>

## Future Work
- 未來希望能將座位表擴充到更大的議場環境，並讓建置方式更有彈性 <br/>
- 提升聲音的品質，讓音頻更加穩定順暢 <br/>
- 主持人選擇方式可改進為一次多人，並就選取的順序來發言 <br/>


