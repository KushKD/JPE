<?php

	/**
	 * CORP CODE:DEMOCORP18
*CORPORATE ACCOUNT NO: 273010200005555
*
*TXN_PAYMODE
*NEFT - NE
*RTGS - RT
*INTERNAL FUND TRANSFER - FT
*IMPS - PA
*CHEQUE - CC
*DEMAND DRAFT  - DD
	 */
	class TestFilesController extends Controller
	{

		 // set the environment so gnupg can find the keyring
    		//putenv("GNUPGHOME=/home/gm8478ofbbdu/.gnupg");


		public function actionPGPEnc(){
			//home/gm8478ofbbdu/.gnupg
			//putenv("GNUPGHOME=/home/gm8478ofbbdu/.gnupg");
			putenv("GNUPGHOME=/tmp");
			
			$text = " ";
			$json = file_get_contents(__DIR__ . '/SampleData/PaymentReq_JSON.txt');
			//$publickey = file_get_contents(__DIR__ . '/SampleData/PublicDIHP.pkr');
			//$fingureprint = file_get_contents(__DIR__ . '/SampleData/fingerprint.txt'); 
			$publickey = file_get_contents(__DIR__ . '/SampleData/new_key.txt');
			//new_key

			

			// $publickey =   "mQENBFxG5R8BCACgVhfkP/Pj1JtjfjlIdxDFn4Ner0EukTHh3VAvIaTHVHRITiTa
			// 				rUB3V8P35D4LB/5oPGvhx0icfDzgt9XTk7qD2TloyMmAX36kmCSPRbD3RU68CQVw
			// 				rbOe5qlnmbmwpRrRoLOssmO6TVKqtz/a9M3s7yQBiIbalZ2sArKSP27NK2npZvq8
			// 				R4dgkU5ZfXnD2O+0f8rN/VOb0/bJkGwF+sNfnBtluKXJeaWSqUQmEKtwCtBKOIar
			// 				Z6wIwDaM0DMrXbR6sgoknDJtd36KVzudINJ9vxJDGx6JVvB9I1FyG6JrS/Ebi0A0
			// 				l34hbfBik00pRBd7D6jcedBziLUfNXqFAJN/ABEBAAG0GGgyaC5zdXBwb3J0QGF4
			// 				aXNiYW5rLmNvbYkBHAQQAQIABgUCXEblHwAKCRB9vTwVSF1sdsXMB/4+X9w5N1fS
			// 				YKR9XTEnVVKE3BO+8XYy+kIOLfxcH8fR/aofiTDVocNrRN6Y3yCvTmXuJLdnCIIJ
			// 				0m8/CdgdubB99bYgInbNLImDZ7nHHl3Br/xtnjOjQzEKwnZIf2SnCmaV8IlwqVZa
			// 				wcgju3zMjsVqa5BQLNGUVUr3llDDHSWnDBLAdta3w0+4KoTHUmhzrVpQukgZV1DS
			// 				LJwCftmLpD3DPFQB3omrcaIluPnQ5obLi7fTV1I6x1ngLitglDaz49zHFWeyh2W4
			// 				RbMQPWG+/eoPBHmKloHAgKS9dris3HjH1zKPq5nnSYqNfvsZgaxmXxBEJqz7UneD
			// 				U54QlVgOvC+G
			// 				=pPzO";


			//publicKey
			
			if(empty($json) &&  empty($publickey) ){
				echo "Nothing Found" ;  

			}else{
				$text = $json;
				// echo "Data from File is:- <pre>\n"  . $text ;
				//Data Encryption Starts Here
				$enc = (null);
				$res = gnupg_init();
				// throw exception if error occurs
				//$res->seterrormode(gnupg::ERROR_EXCEPTION);
				
				//var_dump($res); die();
				//echo "</pre>\n";
				$rtv = gnupg_import($res, $publickey);
				//var_dump($rtv); die();
				//echo "gnupg_import RTV = <br/><pre>\n";
				//var_dump($rtv);
				//echo "</pre>\n";
				// $rtv = gnupg_addencryptkey($res, $fingureprint);
	 			//echo "gnupg_addencryptkey RTV = <br /><pre>\n";
				//var_dump($rtv);
				//echo "</pre>\n";  
				//encrypt_data.txt
				// $encrypt_file =  file_get_contents(__DIR__ . '/SampleData/encrypt_data.txt');
				// $encrypt_file = ""
				$enc = gnupg_encrypt($res, $text); 
				file_put_contents("testenc.txt", $enc);
				var_dump($enc) ;

			}

			//Decrypting Starts Here
			//$res = gnupg_init();
			//$rtv = gnupg_import($res, $publickey);
			//$decrtv = gnupg_adddecryptkey($res, $fingureprint ,"");
  			//$plaintext = gnupg_decrypt($res,$enc);
  			//echo 'Decrypted Data' . $plaintext  . '<br/>'; 
						

		} 


		public function actionHemantTest(){
			   $publickey = '-----BEGIN PGP PUBLIC KEY BLOCK-----

mQENBFxQKDIBCADVNHAvABCTHHxgG6Lei0UVOD7zQZkGj26fcgfFPyhmFCFyXPx0
vwJgbwOLdTmXvo7uo01sfx8vVaAnPFDzrHNKPEsbqGmYyk2V3lflDG+ZysU6UCkX
cqraHvDqn6zcNn9gukZ8dIraDLsRxpXaVE2Iqy5fqpKUfHI8Znql8hGDe7Eaquv0
t+WkgsXRlYNmbFGPqPbuo6PMwxtBllhW/Y3s3IL/b5D1EnEka+neAN5N11MUYbOt
zBwbHi5eL/ebqAHoqkB9BJ27Taag8k3kBBQFGwKsk//kQz0wQQvLMqrvJvoMzQNT
lF367kQ0ecNF7Vp+2N0t6lfc9xC9eqDPdxuRABEBAAG0JWhlbWFudCA8ZXIuaGVt
YW50OTA4dGhha3VyQGdtYWlsLmNvbT6JAVQEEwEIAD4WIQSe5mHTO3O2Co1EDrei
DDuk8QvplwUCXFAoMgIbAwUJA8ODNgULCQgHAgYVCgkICwIEFgIDAQIeAQIXgAAK
CRCiDDuk8Qvpl0J3B/41zuTeuBhIUTf9dOxmK92AGOm4wc4ONAuRkyqMEBljPmg/
CSV9PunFpBxj8M/A7hAYuky8A8qE66c+mu3dFiKWKXTZMiaMbqFL+chWtMdmKOK9
hPpj3bNlLPKe6L6ucXJTV7oEoH5QHMlOtQ6JPwxPBza2a5Mzjjgq2eJCX4ni5C66
CpXg8R1EoVryPx9yj6Rtm4gXMUVUiGALoJmu6Puv0Kv1Yx7d4r49DKQDCfoOPpse
fyRFOcD1qz9bFNxM0xyh6o+5bnv2HBA16BS5mfZyUtIVGRtTuJSh5ZlM9LuUYI99
h0gJWG75BAwEGD3JbJbhph1mQ+ZkhdwkyY1BCVNSuQENBFxQKDIBCADVveT0I33X
zUdsYkDHg/9iut3TTIbgViwXKLwtgF/d/s3msXNMC3B/AcmKEJ3fJz/re54WOCOo
9twmxBsk/flB1hyrF/G395B41DV8/ZhreoSdz4SrfT5Wc0ChyuRvW0svS4DtbXx8
DlOdG6ioJBtxHC0T1mkUd89QDRR3e0qwRg1yaqd39XNc+D9W8c/29YMVV0DaeNOY
r/EPbRkaN8uMvh9P44N6ZFWsBckVv+bEP+Utc7qxJFTdDoq5reZLnjYRWERd9DrU
G3y57MSMbJLCOLHXgYEBpTxcVZw+NCPZxvce3+WnfGkdV3gLukV4yymS70x2sl02
NSA55zp8aYKvABEBAAGJATwEGAEIACYWIQSe5mHTO3O2Co1EDreiDDuk8QvplwUC
XFAoMgIbDAUJA8ODNgAKCRCiDDuk8Qvply2/B/97mZ0rmReMhSgUV/YS+g6aamy7
iNT9Om1GFf6o15FM43fTucMw1LvHRh7+pT9/MMEtmoeYwxD3Q97OVb+6QTFUb4sO
YTouIbELrBvFdF5Y1kCHYruqmcgGhtKPpRHazUZQDT0nM1xLdBBfkcrnFjA7SnCo
wvA/h8AkWVvjpT9vrhYaJbRlGmknw0xeFJxbHGloO4sz2Ts9gVcf+u5YbksffSCW
a08TWehWTNeMTnTbgWsYDyyDiD8+wFUCOa4g7JFKUsnj6/f0PErcKIcpktSAmS9V
uuHMkimbQD5fRRvCyRBpgZAsTlVS8IUI61hZsBuzEpt6DkOx/TaLgRqOV4u5
=C2zG
-----END PGP PUBLIC KEY BLOCK-----';
			   $passphrase = "Test@908";
			  $privatekey = '-----BEGIN PGP PRIVATE KEY BLOCK-----

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
V4u5
=+nl0
-----END PGP PRIVATE KEY BLOCK-----';


			$gpg = new gnupg();
			$text = file_get_contents(__DIR__ . '/SampleData/PaymentReq_JSON.txt'); 
			$privateKeyImport = $gpg->import($privatekey); 
			$gpg->seterrormode(GNUPG_ERROR_WARNING); 
			
			//Sign the Data -- Working
			$gpg->setsignmode(GNUPG_SIG_MODE_DETACH);
			$rtv = $gpg->addsignkey($privateKeyImport['fingerprint']);
			$gpg->seterrormode(GNUPG_ERROR_WARNING); 
			$signed = $gpg->sign($text);
			echo "Signed Data:" . $signed ."<br><br>";
			file_put_contents("signed_JsonPayload_DETACH.txt", $signed);


			//Encrypt Data With Signing
			$importedkey = $gpg->import($publickey);
			$rtv = $gpg->addencryptkey($importedkey['fingerprint']);
			$encS = $gpg->encrypt($signed);
			echo "encrypted Signed Data: ". $encS ."<br><br>";
			file_put_contents("encrypt_data_with_signin_hemant.txt", $encS);

			//Decrypt data with Signing
			$rtv = $gpg->addencryptkey($privateKeyImport['fingerprint']);
			$decS = $gpg->decrypt($encS);
			echo "Decrypted Signed Data: ". $decS ."<br><br>";



			//Encrypt Data Without Sign In
			$importedkey = $gpg->import($publickey);
			$rtv = $gpg->addencryptkey($importedkey['fingerprint']);
			$enc = $gpg->encrypt($text);
			echo "Encrtted Data Without Sign In: ". $enc ."<br><br>";
			file_put_contents("encrypt_data_without_signin_hemant.txt", $enc);


			


			/**
			* decryption process
			* @added Hemant
			*/
			$rtv = $gpg->addencryptkey($privateKeyImport['fingerprint']);
			$dec = $gpg->decrypt($enc);
			echo "Decrypted Data without Sign In: ". $dec ."<br><br>";

			/*
			* decryption end here
			*/

			// //echo $signed ; die;
			// $importedkey = $gpg->import($publickey);
			// $rtv = $gpg->addencryptkey($importedkey['fingerprint']);
			// $encS = $gpg->encrypt($signed);
			// echo "encrypted Signed Data: ". $encS ."<br><br>";

			//Verify Json Payload Normal 
			$signed_json_payload = file_get_contents(__DIR__ . '/SampleData/signed_JsonPayload_normal.txt'); 
			$gpg = new gnupg();
			// clearsigned Working
			//$info = $gpg -> verify($signed_json_payload,false,$text);
			// print_r($info);
			// var_dump($info); 
			// detached signature
			 $gpg->seterrormode(GNUPG_ERROR_WARNING);
			// var_dump($privateKeyImport);
			 $info = $gpg -> verify($signed_json_payload,false,$text);
			 var_dump($info);
			 print_r($info);



			

			
			   
		}



		/**
		* UAT Axis Bank
		* Public Key Provided by Axis Bank
		*Function Working
		**/
		public function actionUatAxisBank(){

			//$text = file_get_contents(__DIR__ . '/UAT_AXIS/PaymentReq_JSON.txt'); 
			$text = $this->CreateJsonPayload();
			// echo "<pre>"; 
			//  print_r(json_encode($text)); die("Text is Here"); 
			$privateKeyDihp =  file_get_contents(__DIR__ . '/UAT_AXIS/PrivateKey_DIHP.txt');;
			$publicKeyDihp =  file_get_contents(__DIR__ . '/UAT_AXIS/PublicKey_DIHP.txt');
			$publicKeyAxis =  file_get_contents(__DIR__ . '/UAT_AXIS/DIHP_UAT_01022019.pkr');

			//Sign the Data Using DIHP Private Key
			// $gpg = new gnupg(); 
			// $privateKeyImport = $gpg->import($privateKeyDihp); 
			// $gpg->seterrormode(GNUPG_ERROR_WARNING); 
			
			//Sign the Data -- Working
			// $gpg->setsignmode(GNUPG_SIG_MODE_DETACH);
			// $rtv = $gpg->addsignkey($privateKeyImport['fingerprint']);
			// $gpg->seterrormode(GNUPG_ERROR_WARNING); 
			// $signedText = $gpg->sign($text);
			// echo "Signed Data:" . $signedText ."<br><br>";
			// file_put_contents("signed_JsonPayload_UAT_Axis.txt", $signedText);


			//Encrypt Data With Signing
			// $importedkey = $gpg->import($publicKeyAxis); //Earlier Public Key Axis Bank
			// $rtv = $gpg->addencryptkey($importedkey['fingerprint']);
			// $encS = $gpg->encrypt($signedText);  //signedText
			// echo "encrypted Signed Data: ". $encS ."<br><br>";
			// file_put_contents("encrypt_data_with_signin_uatAxis.txt", $encS);


			
			/**
			* Encryption and SignIn process
			* @added Kush Kumar Dhawan 
			*/
			$gpg = new gnupg();
			//$gpg->setsignmode(GNUPG_SIG_MODE_DETACH);
			$privateKeyImport = $gpg->import($privateKeyDihp); 
			$publicKeyImport= $gpg->import($publicKeyAxis); 
			$gpg -> addencryptkey($publicKeyImport['fingerprint']);
			$gpg -> addsignkey($privateKeyImport['fingerprint']);
			$enc = $gpg -> encryptsign(json_encode($text));
			//$enc = $gpg -> encryptsign($text);
			file_put_contents("encryptandsign.txt", $enc);
			echo $enc ."<br><br>";

			//Pass the Data to Dervice $enc and get the response

			$responseFromServer = $this->PostDataToServer($enc);
 
			


			/**
			* Verify Sign before Decryption
			*@added Kush Kumar Dhawan
			*/
			$encrypted_text_to_verify = file_get_contents(__DIR__ . '/UAT_AXIS/Sign_and_Enc.txt');
			$gpg = new gnupg();
			// clearsigned Working
			//$info = $gpg -> verify($signed_json_payload,false,$text);
			// print_r($info);
			// var_dump($info); 
			// detached signature
			 $gpg->seterrormode(GNUPG_ERROR_WARNING);
			 echo($importedkey['fingerprint']) ."<br></br>";   
			// $info = $gpg -> verify($encrypted_text_to_verify,false);
			 $info = $gpg -> verify($encrypted_text_to_verify,false);
			// var_dump($info); //die("ui") ;
			// echo($info[0]['fingerprint']);


			 if($importedkey['fingerprint'] = $info[0]['fingerprint']){
			 	/**
			* Decryption Process  if  $importedkey['fingerprint']  ===  $info[0]['fingerprint']
			*@added Kush Kumar Dhawan 
			*/
			$encrypted_text = file_get_contents(__DIR__ . '/UAT_AXIS/Sign_and_Enc.txt');
			echo "Encrypted  Data: ". $encrypted_text ."<br><br>";
			$gpg = new gnupg();
			$gpg -> adddecryptkey($privateKeyImport['fingerprint'],"");
			$plain = $gpg -> decrypt($encrypted_text);  //Get the Encrypted text from Axis Bank
			echo "Decrypted  Data: ". $plain ."<br><br>";
		}else{
			echo "Sign In Verification failed.  Data <br><br>";
		}

			
			
			
			   
		}
   

		public function PostDataToServer($enc){
				//echo $enc; die("Yahan");

				// Endpoint - https://qah2h.axisbank.co.in/RESTAdapter/AxisBank/Dihp/Pay
				// user name- corpuser
				// password- will be shared across call

				$url = "https://qah2h.axisbank.co.in/RESTAdapter/AxisBank/Dihp/Pay";
				$username = "corpuser";
				$password = "axiscorpcon1!"; //axiscorp1!

				$ch = curl_init();
				curl_setopt($ch, CURLOPT_URL, $url);
				curl_setopt( $ch, CURLOPT_POSTFIELDS, $enc);
				curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
				curl_setopt($ch, CURLOPT_USERPWD, "$username:$password");
				curl_setopt($ch, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
				$output = curl_exec($ch);
				$info = curl_getinfo($ch);
				curl_close($ch);
				 var_dump($output);

				//  if ($output === false) {
    //     throw new Exception(curl_error($ch), curl_errno($ch));
    // }

				print_r($info);print_r($output); die("Yo Yo YO");


		}







//$this->loadModel($submissionID);
public function CreateJsonPayload(){

			$PAYMENTS = array();
			$JSON=array();		
			$JSON['RECORD']['PAYMENT_DETAILS'][]=array('PAYMENTS'=>array( 
																	'API_VERSION'=> "1",
																	'CORP_CODE'=> "DEMOCORP18",
																	'CMPY_CODE'=> "",
																	'TXN_CRNCY'=> "INR",
																	'TXN_PAYMODE'=> "NE",
																	'CUST_UNIQ_REF'=> "AJAX20181112125752705489",
																	'TXN_TYPE'=> "",
																	'TXN_AMOUNT'=> "1",
																	'CORP_ACC_NUM'=> "273010200005555",
																	'CORP_IFSC_CODE'=> "",
																	'ORIG_USERID'=> "",
																	'USER_DEPARTMENT'=> "",
																	'TRANSMISSION_DATE'=> "06-02-2019 20-26-19",
																	'BENE_CODE'=> "33416ae2",
																	'VALUE_DATE'=> "06-02-2019",
																	'RUN_IDENTIFICATION'=> "",
																	'FILE_NAME'=> "",
																	'BENE_NAME'=> "Kush",
																	'BENE_ACC_NUM'=> "31136695259",
																	'BENE_IFSC_CODE'=> "SBIN0007115",
																	'BENE_AC_TYPE'=> "",
																	'BENE_BANK_NAME'=> "",
																	'BASE_CODE'=> "DEMOCORP",
																	'CHEQUE_NUMBER'=> "",
																	'CHEQUE_DATE'=> array(),  
																	'PAYABLE_LOCATION'=> "",
																	'PRINT_LOCATION'=> "",
																	'PRODUCT_CODE'=> "",
																	'BATCH_ID'=> "1",
																	'BENE_ADDR_1'=> "",
																	'BENE_ADDR_2'=> "",
																	'BENE_ADDR_3'=> "",
																	'BENE_CITY'=> "",
																	'BENE_STATE'=> "",
																	'BENE_PINCODE'=> "",
																	'CORP_EMAIL_ADDR'=> "",
																	'BENE_EMAIL_ADDR1'=> "",
																	'BENE_EMAIL_ADDR2'=> "",
																	'BENE_MOBILE_NO'=> "",
																	'ENRICHMENT1'=> "",
																	'ENRICHMENT2'=> "",
																	'ENRICHMENT3'=> "",
																	'ENRICHMENT4'=> "",
																	'ENRICHMENT5'=> "",
																	'STATUS_ID'=> "" 
																		  
																),
															"INVOICE"=>array(array("INVOICE_NUMBER"=>"",
																					"INVOICE_DATE"=>"",
																					"NET_AMOUNT" => "",
																					"TAX" => "",
																					"CASH_DISCOUNT" => "",
																					"INVOICE_AMOUNT" => ""
																				)
																			)
																		);


		
			return $JSON;

		}








	}

?>
