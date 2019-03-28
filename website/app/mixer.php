<?php

class Mixer
{
	public $scopes = ["channel:follow:self"];
	public $mixerAPI = "https://mixer.com/api/v1";

	public function GetToken()
	{
		$provider = new \League\OAuth2\Client\Provider\GenericProvider([
			'clientId'					=> $_SERVER['mixer_client_id'],
			'clientSecret'				=> $_SERVER['mixer_client_secret'],
			'redirectUri'				=> "https://sign-up.mc-gdu.mooo.com/login",
			'urlAuthorize'				=> "https://mixer.com/oauth/authorize",
			'urlAccessToken'			=> "https://mixer.com/api/v1/oauth/token",
			'urlResourceOwnerDetails'	=> null,
			'scopes'					=> $this->scopes,
			'scopeSeparator'			=> ', '
		]);

		// We don't have an auth code, get one.
		if(!isset($_GET['code']))
		{
			$authorizationUrl =$provider->getAuthorizationUrl();

			$_SESSION['oauth2state'] = $provider->getState();

			// Redirect the user to the Auth URL over at mixer.
			header('location: ' . $authorizationUrl);
			exit;
		}
		// Check given state against previously stored one to mitigate CSRF attack
		elseif (empty($_GET['state']) || (isset($_SESSION['oauth2state']) && $_GET['state'] !== $_SESSION['oauth2state']))
		{
			if(isset($_SESSION['oauth2state']))
			{
				unset($_SESSION['oauth2state']);
			}

			exit('Invalid State');
		}
		else
		{
			try {
				$accessToken = $provider->getAccessToken('authorization_code', [
					'code'	=> $_GET['code']
				]);
				echo "<pre>";
				echo "\nAccess Token:		" . $accessToken->getToken();
				echo "\nRefresh Token:		" . $accessToken->getRefreshToken();
				echo "\nExpired in:			" . $accessToken->getExpires();
				echo "\nAlready expired:	" . $accessToken->hasExpired();
				echo "</pre>";

				$resourceOwner = $provider->getResourceOwner($accessToken);

				var_export($resourceOwner->toArray());

				$request = $provider->getAuthenticatedRequest(
					'GET',
					"$this->mixerAPI/users/current",
					$accessToken
				);
			}
			catch (\League\OAuth2\Client\Provider\Exception\IdentityProviderException $e)
			{
				throw($e);
			}
		}
	}

	public function IsFollowing($uderID)
	{
		
	}
}
?>
