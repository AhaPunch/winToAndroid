import tkinter as tk
from tkinter import ttk, messagebox
import socket

class TextSenderApp:
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("文本发送器")
        
        # 创建UI元素
        self.create_widgets()
        
        self.root.mainloop()
    
    def create_widgets(self):
        # IP地址输入框
        ip_frame = ttk.Frame(self.root)
        ip_frame.pack(pady=5, padx=10, fill='x')
        
        ttk.Label(ip_frame, text="IP地址:").pack(side='left')
        self.ip_entry = ttk.Entry(ip_frame)
        self.ip_entry.pack(side='left', padx=5)
        self.ip_entry.insert(0, "192.168.1.100")
        
        ttk.Label(ip_frame, text="端口:").pack(side='left')
        self.port_entry = ttk.Entry(ip_frame, width=10)
        self.port_entry.pack(side='left', padx=5)
        self.port_entry.insert(0, "8888")
        
        # 文本输入区域
        self.text_input = tk.Text(self.root, height=10, width=40)
        self.text_input.pack(pady=10, padx=10)
        
        # 发送按钮
        self.send_button = ttk.Button(self.root, text="发送到手机", command=self.send_text)
        self.send_button.pack(pady=5)
    
    def send_text(self):
        text = self.text_input.get(1.0, tk.END).strip()
        if not text:
            messagebox.showwarning("警告", "请输入要发送的文字")
            return
        
        try:
            ip = self.ip_entry.get()
            port = int(self.port_entry.get())
            
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                s.connect((ip, port))
                s.sendall((text + "\n").encode('utf-8'))
            
            messagebox.showinfo("成功", "文字已发送到手机")
        except Exception as e:
            messagebox.showerror("错误", f"发送失败: {str(e)}")

if __name__ == "__main__":
    app = TextSenderApp() 