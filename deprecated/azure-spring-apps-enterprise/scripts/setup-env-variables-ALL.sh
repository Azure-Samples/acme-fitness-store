#
# Cumalative wrapper shell script that will SOURCE the setup-x scripts in a specified order.
#

  #set -x	

	scriptz_array='[
		{"scriptName":"./setup-env-variables.sh"},
		{"scriptName":"./setup-sso-variables-ad.sh"},
		{"scriptName":"./setup-sso-variables.sh"},
		{"scriptName":"./setup-db-env-variables.sh"},
		{"scriptName":"./setup-ai-env-variables.sh"},
		{"scriptName":"./setup-storage-env-variables.sh"}
	]'

	usage(){
   			echo " "
   			echo "Sources the following files in this order:"
   			echo " "
   			
				for element in $(echo "${scriptz_array}" | jq -r '.[] | @base64'); do
				
					  scriptName=""
				
				    _jq() 
				    {
				     	echo ${element} | base64 --decode | jq -r ${1}
				    }
				
					  scriptName=$(_jq '.scriptName')			

					  echo "$scriptName"			
				   
				done   			
				
   			echo " "   			
   			echo " "   			   							
   			echo "Usage: source $0 "
   			echo " "   			
   			echo " OR "
   			echo " "   			
   			echo "Usage: . $0 "   			
   			echo " "
   			echo " "
   			
		exit 1
	}
	
	
	i=0
	for argz in "$@"
	do

	 		if [ $i -eq 0 ]
	   		then
					case "$argz" in


					    -H)
									usage
									exit 3
					        ;;

					    -h)
									usage
									exit 3
									;;

					    *)

					esac

			fi

			((i=i+1))

	done
		

  baseCommand="source "
  theCommand=""

	for element in $(echo "${scriptz_array}" | jq -r '.[] | @base64'); do
	
		  scriptName=""
	
	    _jq() 
	    {
	     	echo ${element} | base64 --decode | jq -r ${1}
	    }
	
		  scriptName=$(_jq '.scriptName')

			theCommand=$baseCommand$scriptName
				
		  echo ""
		  echo "Running: $theCommand"

	   	# Set this to 7 to start..
	   	scriptrc=7
			
			$theCommand
			scriptrc=$?
			
			if ([ $scriptrc -eq 0 ] )
			   then

		     	 echo "ScriptName: $theCommand -SUCCESS"		 				
		
			else
		    if ([ $greprc -eq 1 ] )
		       then
						
		 				echo "ScriptName: $theCommand  -FAILED!!!"
		
		    else
		        echo "Some sort of error!!"
		    fi
		
			fi	
	   
    	echo ""		 				
	   
	done
			
	


