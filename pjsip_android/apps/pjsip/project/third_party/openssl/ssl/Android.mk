LOCAL_PATH:= $(call my-dir)

#######
# SSL #
#######
include $(CLEAR_VARS)
LOCAL_MODULE:= ssl 

LOCAL_C_INCLUDES :=  $(LOCAL_PATH)/../  $(LOCAL_PATH)/../include  $(LOCAL_PATH)/../crypto
LOCAL_SRC_FILES := s2_meth.c s2_srvr.c s2_clnt.c \
	s2_lib.c s2_enc.c s2_pkt.c s3_meth.c s3_srvr.c s3_clnt.c s3_lib.c s3_enc.c s3_pkt.c s3_both.c \
	s23_meth.c s23_srvr.c s23_clnt.c s23_lib.c s23_pkt.c t1_meth.c t1_srvr.c t1_clnt.c t1_lib.c \
	t1_enc.c ssl_lib.c ssl_err2.c ssl_cert.c ssl_sess.c ssl_ciph.c ssl_rsa.c  \
	 ssl_algs.c  ssl_err.c 

# Useless for pjsip 
# ssl_stat.c ssl_asn1.c ssl_txt.c bio_ssl.c kssl.c


include $(LOCAL_PATH)/../android-config.mk

include $(BUILD_STATIC_LIBRARY)

