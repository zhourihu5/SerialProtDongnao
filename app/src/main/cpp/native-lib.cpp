#include <jni.h>
#include <string>
#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>


#include "android/log.h"
static const char *TAG="david";
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)
extern "C"
JNIEXPORT jobject JNICALL
Java_com_dongnao_serialprotdongnao_SerialPortManager_open(JNIEnv *env, jobject instance,
                                                          jstring path_, jint baudRate) {
    const char *path = env->GetStringUTFChars(path_, 0);
    int fd;
    speed_t speed;
    jobject mFileDescriptor;
    speed_t spped = B115200;
    jboolean iscopy;
    fd = open(path, O_RDWR);
    if (fd == -1)
    {
        LOGE("打开失败");
        env->ReleaseStringUTFChars(path_, path);
        return NULL;
    }
    struct termios cfg;
    LOGE("------------>1    ");
//    获取和设置终端属性
    /**
     * TCSANOW：不等数据传输完毕就立即改变属性。
TCSADRAIN：等待所有数据传输结束才改变属性。
TCSAFLUSH：清空输入输出缓冲区才改变属性。
注意：当进行多重修改时，应当在这个函数之后再次调用 tcgetattr() 来检测是否所有修改都成功实现。
     */
    if (tcgetattr(fd, &cfg))
    {
        close(fd);
        env->ReleaseStringUTFChars(path_, path);
        LOGE("获取终端属性失败");
        return NULL;
    }
    cfmakeraw(&cfg);
//    设置串口读取波特率
    cfsetispeed(&cfg, speed);
    //    设置串口写入波特率
    cfsetospeed(&cfg, speed);

    if (tcsetattr(fd, TCSANOW, &cfg))
    {
        close(fd);
        env->ReleaseStringUTFChars(path_, path);
        LOGE("设置终端属性失败");
        return NULL;
    }
    jclass cFileDescriptor = env->FindClass( "java/io/FileDescriptor");
    jmethodID iFileDescriptor = env->GetMethodID( cFileDescriptor, "<init>", "()V");
    jfieldID descriptorID = env->GetFieldID(cFileDescriptor, "descriptor", "I");
    mFileDescriptor = env->NewObject(cFileDescriptor, iFileDescriptor);
    env->SetIntField( mFileDescriptor, descriptorID, (jint)fd);

    env->ReleaseStringUTFChars(path_, path);
    return mFileDescriptor;



    env->ReleaseStringUTFChars(path_, path);
}