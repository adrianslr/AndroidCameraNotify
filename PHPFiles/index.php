<?php 

$dir    = 'img/';

$files = scandir( $dir );

foreach( $files as $file )
{
	if( strpos( $file, '.png' ) !== false )
	{
		echo '<a href="img/'.$file.'">'.$file.'</a><br>';
	}
}

?>