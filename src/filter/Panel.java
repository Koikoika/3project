	package fourier;

	import java.awt.image.BufferedImage;
	import javax.swing.JButton;
	import javax.swing.*;  
	import java.awt.event.*;
	import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
	import org.opencv.core.Point;
	import org.opencv.core.Scalar;
	import org.opencv.core.Size;
	import org.opencv.imgcodecs.Imgcodecs;
	import org.opencv.videoio.*; 
	import org.opencv.imgproc.Imgproc;
	import java.awt.Graphics;
    import java.awt.event.ActionListener;
	import java.util.Timer;
	import java.util.TimerTask;
	
	public class Panel extends JPanel{  
		
		 //int count = 0;
		// Mat webcam_image=new Mat();  
		
		private static final long serialVersionUID = 1L;  
		private BufferedImage image;  
		// Create a constructor method  
		public Panel(){  
			super();  
		}  
		private BufferedImage getimage(){  
			return image;  
		}  
		private void setimage(BufferedImage newimage){  
			image=newimage;  
			return;  
		}  
		/**  
		 * Converts/writes a Mat into a BufferedImage.  
		 *  
		 * @param matrix Mat of type CV_8UC3 or CV_8UC1  
		 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY  
		 */  
		public static BufferedImage matToBufferedImage(Mat matrix) {  
			int cols = matrix.cols();  
			int rows = matrix.rows();  
			int elemSize = (int)matrix.elemSize();  
			byte[] data = new byte[cols * rows * elemSize];  
			int type;  
			
			matrix.get(0, 0, data);  
			
			
			switch (matrix.channels()) {  
			case 1:  
				type = BufferedImage.TYPE_BYTE_GRAY;  
				break;  
			case 3:  
				type = BufferedImage.TYPE_3BYTE_BGR;  
				// bgr to rgb  
				byte b;  
				for(int i=0; i<data.length; i=i+3) {  
					b = data[i];  
					data[i] = data[i+2];  
					data[i+2] = b;  
				}  
				break;  
			default:  
				return null;  
			}  
			
			
			BufferedImage image2 = new BufferedImage(cols, rows, type);  
			image2.getRaster().setDataElements(0, 0, cols, rows, data);  
			return image2;  
			
		}
		
		public void paintComponent(Graphics g){  
			BufferedImage temp=getimage();
			if(temp!=null){
				g.drawImage(temp,20,20,temp.getWidth(),temp.getHeight(), this);  
			}
		} 
		
		//カメラの動画を反転させる
		public static BufferedImage createMirrorImage(BufferedImage temp){
			int width = temp.getWidth();
			int height = temp.getHeight();
			int size = width * height;
			int []buf = new int[ size ];
			temp.getRGB(0, 0, width, height, buf, 0, width);   //イメージを配列に変換

			//bufのイメージ配列で、左右を変換する。
			int x1, x2, temp2;
			for(int y = 0; y < size; y+=width){
				x1 = 0;
				x2 = width -1;
				while(x1 < x2){// 交換の繰り返し
					temp2 = buf[y+x1];
					buf[y+x1++] = buf[y+x2];
					buf[y+x2--] = temp2;
				}
			}
			BufferedImage img2= new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
			img2.setRGB(0, 0, width, height, buf, 0, width);//配列をイメージに書き込む
			return img2;
		}
		
				
		public static void main(String arg[]){  
			// Load the native library.  
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			JFrame frame = new JFrame("BasicPanel");  
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
			frame.setSize(400,400);  
			Panel panel = new Panel();  
			frame.setContentPane(panel);       
			frame.setVisible(true);       
			 Mat webcam_image = new Mat(); 
			 Mat webcam_image2 = new Mat(); //ウィンド表示用
			 Mat output = new Mat();
			 Mat sample = new Mat();
			 BufferedImage img; 
			 BufferedImage img2; 
		     BufferedImage imgRev; 
			 int count = 0;
			
			VideoCapture capture =new VideoCapture(0);  
			 JButton button1 = new JButton("user1");
			 JButton button2 = new JButton("user2");
			 JButton button3 = new JButton("user3");
			 JButton open = new JButton("open");
			 
			 frame.add(button1);
			 frame.add(button2);
			 frame.add(button3);
			 frame.add(open); 
			 
			 
			 
			
			 
			 button1.addActionListener(
				      new ActionListener(){
				        public void actionPerformed(ActionEvent event){
				          JLabel msg = new JLabel("user1の物体の登録を開始します。(約10秒間)");
				          JOptionPane.showMessageDialog(frame, msg);
				          //JLabel tmr = new JLabel();
				          //int data[] = {1,2,3,4,5,6,7,8,9,10};				           
				          Timer timer = new Timer(false);
				        	  TimerTask task = new TimerTask() {
				      			int cnt = 1;
				      			@Override
				      			public void run() {
				      				//ここに定期実行させたい処理を記述
				      				Imgcodecs.imwrite("/Users/nakamurayuuka/Documents/3pro/user/1/"+ String.valueOf(cnt) +".png", webcam_image);	
				      				cnt++;
				      				
				    				//10回実行で停止
				    				if ( cnt >= 11 ) {
				    					timer.cancel();
				    					JLabel end = new JLabel("次に物体の軌跡の登録をします(3秒)");
								          JOptionPane.showMessageDialog(frame, end);
								          
								          //軌跡の登録、まず３秒くらいで１０枚？（適宜)画像を作る(本当はトラッキングされた四角）、今はサンプル
								          //そして、その後でaddWeightedで全イメージかけあわせ。
								          
								          
				    				}
				    				
				      			}
				      			
				      		};
				      		
				      		timer.schedule(task, 0,1000);
				      		
				          } 
				       
				        }				      
				    );
			
			 button2.addActionListener(
				      new ActionListener(){
				        public void actionPerformed(ActionEvent event){
				          JLabel msg = new JLabel("user2の物体の登録を開始します。(約10秒間)");
				          JOptionPane.showMessageDialog(frame, msg);				          
				          //int data[] = {1,2,3,4,5,6,7,8,9,10};				           
				          Timer timer = new Timer(false);
				        	  TimerTask task = new TimerTask() {
				      			int cnt = 1;
				      			@Override
				      			public void run() {
				      				//ここに定期実行させたい処理を記述
				      				//Imgproc.putText(webcam_image,String.valueOf(cnt),new Point(100, 100), 2,1,new Scalar(0, 0, 225));//カウント
				      				Imgcodecs.imwrite("/Users/nakamurayuuka/Documents/3pro/user/2/"+ String.valueOf(cnt) +".png", webcam_image);	
				      				cnt++;
				    				//10回実行で停止
				    				if ( cnt >= 11 ) {
				    					timer.cancel();
				    					JLabel end = new JLabel("次に物体の軌跡の登録をします");
								          JOptionPane.showMessageDialog(frame, end);
								          
								        // 軌跡の登録 
								          
								          
				    				}
				      			}
				      		};
				      		timer.schedule(task, 0, 1000);
				          }           
				        }				      
				    );
	       
			 button3.addActionListener(
				      new ActionListener(){
				        public void actionPerformed(ActionEvent event){
				          JLabel msg = new JLabel("user3の物体の登録を開始します。(約10秒間)");
				          JOptionPane.showMessageDialog(frame, msg);	
				          
				         		           
				          Timer timer = new Timer(false);
				        	  TimerTask task = new TimerTask() {
				      			int cnt = 1;
				      			@Override
				      			public void run() {
				      				//ここに定期実行させたい処理を記述
				      				//Imgproc.putText(webcam_image,String.valueOf(cnt),new Point(100, 100), 2,1.2,new Scalar(0, 0, 225));//カウント
				      				Imgcodecs.imwrite("/Users/nakamurayuuka/Documents/3pro/user/3/"+ String.valueOf(cnt) +".png", webcam_image);
				      				
				      				cnt++;
				    				//10回実行で停止
				    				if ( cnt >= 11 ) {
				    					timer.cancel();
				    					JLabel end = new JLabel("次に物体の軌跡の登録をします");
								          JOptionPane.showMessageDialog(frame, end);
								          
								          //軌跡の登録
								          //軌跡のトラッキング具体的に？写真から創造する
								          
								          
				    				}
				      			}
				      		};
				      		timer.schedule(task, 0, 1000);
				          }           
				        }				      
				    );
			
			 
			 open.addActionListener(
				      new ActionListener(){
				        public void actionPerformed(ActionEvent event){
				        	//画像2,3まいとって、色が違ったら認証できません　見たことある色だったら　軌跡を描いてください。
				        	JLabel msg = new JLabel("物体認証を開始します");
					          JOptionPane.showMessageDialog(frame, msg);	
					          
					         			           
					          Timer timer = new Timer(false);
					        	  TimerTask task = new TimerTask() {
					      			int cnt = 1;
					      			@Override
					      			public void run() {
					      				//ここに定期実行させたい処理を記述
					      				
					      				//Imgproc.putText(webcam_image,String.valueOf(cnt),new Point(100, 100), 2,1.2,new Scalar(0, 0, 225));//カウント
					      				Imgcodecs.imwrite("/Users/nakamurayuuka/Documents/3pro/open/"+ String.valueOf(cnt) +".png", webcam_image);
					      				cnt++;
					      				
					    				//3回実行で停止
					    				if ( cnt >= 4 ) {
					    					timer.cancel();
					    					  JLabel end = new JLabel("物体認証に成功しました。次に物体の軌跡を描いて下さい");
									          JOptionPane.showMessageDialog(frame, end);
  
					    				}
					    				
					      			}
					      		};
					      		timer.schedule(task, 0, 1000);
				        	
				        }           
				        }				      
				    );
			
			 
			if( capture.isOpened())  
			{  
				while( true )  
				{  
	
					capture.read(webcam_image);  
					if( !webcam_image.empty() )  
					{  
						
						
						//元々0.3で、0.6で大体画面いっぱい
						Imgproc.resize(webcam_image, webcam_image, new Size(webcam_image.size().width*0.6,webcam_image.size().height*0.6));
						frame.setSize(webcam_image.width()+40,webcam_image.height()+60); 
						//Button obj = new Button();
						//obj.count++;
						img2=matToBufferedImage(webcam_image);
					
						webcam_image2 = webcam_image.clone();
						//登録用中央に赤い四角を表示する。以下3行の座標はのちに反転されるため右上が(0,0)     
						//四角の中心(575,325)
				        Imgproc.rectangle(webcam_image2,new Point(325, 75),new Point(825, 575),new Scalar(0, 0, 225),8,8,0);
						Imgproc.line(webcam_image2,new Point(575, 75),new Point(575, 575),new Scalar(0, 0, 225));//縦
						Imgproc.line(webcam_image2,new Point(325, 325),new Point(825, 325),new Scalar(0, 0, 225));//横
						//ここまでが四角描写なので登録時以外は消す
						img=matToBufferedImage(webcam_image2); 
						imgRev = createMirrorImage(img);//matからイメージに変換してから反転させる						
	                    panel.setimage(imgRev);  	                   
						panel.repaint();
					
						
						
						 
					
					}  
					else  
					{  
						System.out.println(" --(!) No captured frame -- ");  
					}  
				}  
			}  
			return;  
				
			
			 
	         
		}
		
		
	}  
	
