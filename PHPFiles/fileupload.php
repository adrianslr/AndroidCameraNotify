<?php
   if( isset( $_FILES[ 'uploaded_file' ] ) )
   {
      move_uploaded_file( $_FILES['uploaded_file']['tmp_name'], "img/".md5( time( ) ).".png" );
   }
?>