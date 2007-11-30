<?php
/*
Plugin Name: Alternate comment form
Plugin URI: http://www.sothq.net
Description: Allows for an alternate comment form next to the existing one. Based on Reverse Order Comments.
Author: Arnan de Gans
Version: 0.1
Author URI: http://www.sothq.net
*/ 

#---------------------------------------------------
# Only proceed with the plugin if MySQL Tables are setup properly
#---------------------------------------------------
acf_check_config();
add_action('admin_menu', 'acf_add_pages'); //Add page menu links

if(isset($_POST['acf_submit_options']) AND $_GET['updated'] == "true") {
	add_action('init', 'acf_options_submit'); //Update Options
}

if(isset($_POST['acf_uninstall'])) {
	add_action('init', 'acf_plugin_uninstall'); //Uninstall
}

// Load Options
$acf_config = get_option('acf_config');

/*-------------------------------------------------------------
 Name:      acf_comments_template

 Purpose:   Admin management page
 Receive:   $file
 Return:    -none-
-------------------------------------------------------------*/
function acf_comments_template( $file = '/comments.php' ) {
	global $wp_query, $withcomments, $post, $wpdb, $id, $comment, $user_login, $user_ID, $user_identity, $acf_config;

	if ( is_single() || is_page() || $withcomments ) :
		$req = get_settings('require_name_email');
		$comment_author = isset($_COOKIE['comment_author_'.COOKIEHASH]) ? trim(stripslashes($_COOKIE['comment_author_'.COOKIEHASH])) : '';
		$comment_author_email = isset($_COOKIE['comment_author_email_'.COOKIEHASH]) ? trim(stripslashes($_COOKIE['comment_author_email_'.COOKIEHASH])) : '';
		$comment_author_url = isset($_COOKIE['comment_author_url_'.COOKIEHASH]) ? trim(stripslashes($_COOKIE['comment_author_url_'.COOKIEHASH])) : '';
	if ( empty($comment_author) ) {
		$comments = $wpdb->get_results("SELECT * FROM $wpdb->comments WHERE comment_post_ID = '$post->ID' AND comment_approved = '1' ORDER BY comment_date ".$acf_config['sortorder']);
	} else {
		$author_db = addslashes($comment_author);
		$email_db  = addslashes($comment_author_email);
		$comments = $wpdb->get_results("SELECT * FROM $wpdb->comments WHERE comment_post_ID = '$post->ID' AND ( comment_approved = '1' OR ( comment_author = '$author_db' AND comment_author_email = '$email_db' AND comment_approved = '0' ) ) ORDER BY comment_date ".$acf_config['sortorder']);
	}

	get_currentuserinfo();

	define('ACF_COMMENTS_TEMPLATE', true);
	$include = apply_filters('comments_template', TEMPLATEPATH . $file );
	if ( file_exists( $include ) )
		require( $include );
	else
		require( ABSPATH . 'wp-content/themes/default/comments.php');

	endif;
}

/*-------------------------------------------------------------
 Name:      acf_add_pages

 Purpose:   Add pages to admin menus
 Receive:   -none-
 Return:    -none-
-------------------------------------------------------------*/
function acf_add_pages() {
	global $events_config;

	add_options_page('Alt comment form', 'Alt comment form', 10, basename(__FILE__), 'acf_options_page');
}

/*-------------------------------------------------------------
 Name:      acf_options_page

 Purpose:   Admin options page
 Receive:   -none-
 Return:    -none-
-------------------------------------------------------------*/
function acf_options_page() {
	$acf_config = get_option('acf_config');
?>
		<div class="wrap">
		  	<h2>Alternate comment form options</h2>
		  	<form method="post" action="<?php echo $_SERVER['REQUEST_URI'];?>&amp;updated=true">
		    	<input type="hidden" name="acf_submit_options" value="true" />
		    	<table width="100%" cellspacing="2" cellpadding="5" class="editform">
		    	
		      	<tr>
			        <th width="33%" scope="row">Order them...</th>
			        <td><select name="acf_sortorder">';
				        <option value="ASC" <?php if($acf_config['sortorder'] == "ASC") { echo 'selected'; } ?>>ascending</option>
				        <option value="DESC" <?php if($acf_config['sortorder'] == "DESC") { echo 'selected'; } ?>>descending</option>
					</select></td>
		      	</tr>	      	

		    	</table>
			    <p class="submit">
			      	<input type="submit" name="Submit" value="Update Options &raquo;" />
			    </p>
			</form>

		  	<h2>Uninstaller</h2>
		  	<p>Some values installed in the database are only required for this plugin, they can and should be removed if you no longer use it. Use the uninstaller to do this.</p>
		  	<p><b>WARNING! -- This process is irreversible and will remove the options associated with Alternate Comment Form!</b></p>
		  	<p>For the techies: Upon un-installation the acf_config record in the wp_options table will be deleted.</p>
		  	<form method="post" action="<?php echo $_SERVER['REQUEST_URI'];?>">
		  		<p class="submit">
		    	<input type="hidden" name="acf_uninstall" value="true" />
		    	<input onclick="return confirm('You are about to uninstall the alternate comment form plugin\n\'OK\' to continue, \'Cancel\' to stop.')" type="submit" name="Submit" value="Uninstall Plugin &raquo;" />
		  		</p>
		  	</form>

		</div>
<?php
}

/*-------------------------------------------------------------
 Name:      acf_check_config

 Purpose:   Create or update the options
 Receive:   -none-
 Return:    -none-
-------------------------------------------------------------*/
function acf_check_config() {
	if ( !$option = get_option('acf_config') ) {
		// Default Options
		$option['sortorder'] 		= 'ASC';
		update_option('acf_config', $option);
	}

	// If value not assigned insert default (upgrades)
	if (strlen($option['sortorder']) < 1) {
		$option['sortorder'] 		= 'ASC';
		update_option('acf_config', $option);
	}
}

/*-------------------------------------------------------------
 Name:      acf_options_submit

 Purpose:   Save options
 Receive:   $_POST
 Return:    -none-
-------------------------------------------------------------*/
function acf_options_submit() {
	//options page
	$option['sortorder'] 		= $_POST['acf_sortorder'];
	update_option('acf_config', $option);
}

/*-------------------------------------------------------------
 Name:      acf_plugin_uninstall

 Purpose:   Delete the options on uninstall.
 Receive:   -none-
 Return:	-none-
-------------------------------------------------------------*/
function acf_plugin_uninstall() {
	global $wpdb;

	// Delete Option
	delete_option('acf_config');

	// Deactivate Plugin
	$current = get_settings('active_plugins');
    array_splice($current, array_search( "wp-alternate-commentform.php", $current), 1 );
	update_option('active_plugins', $current);
	do_action('deactivate_' . trim( $_GET['plugin'] ));

	die();

}
?>