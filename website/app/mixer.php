<?php

class Mixer
{
	public $scopes = ["channel:follow:self"];
	public $mixerAPI = "https://mixer.com/api/v1";

	public $provider;
	public $accessToken;

	public function __construct()
	{
		$this->provider = new \League\OAuth2\Client\Provider\GenericProvider([
			'clientId'					=> $_SERVER['mixer_client_id'],
			'clientSecret'				=> $_SERVER['mixer_client_secret'],
			'redirectUri'				=> "https://sign-up.mc-gdu.mooo.com/login",
			'urlAuthorize'				=> "https://mixer.com/oauth/authorize",
			'urlAccessToken'			=> "https://mixer.com/api/v1/oauth/token",
			'urlResourceOwnerDetails'	=> null,
			'scopes'					=> $this->scopes,
			'scopeSeparator'			=> ', '
		]);
	}

	public function GetToken()
	{
		if(!isset($_SESSION) || $_SESSION['access-token'] == null)
		{
			$this->GetNewToken();
		}
		else
		{
			$this->accessToken = $_SESSION['access-token'];

			if($this->accessToken->hasExpired())
			{
				$this->accessToken = $this->provider->getAccessToken('refresh_token', [
					'refresh_token' => $this->accessToken->getRefreshToken()
				]);

				$_SESSION['access-token'] = $this->accessToken;
			}
		}

	}
	function GetNewToken()
	{
		// We don't have an auth code, get one.
		if(!isset($_GET['code']))
		{
			$authorizationUrl =$this->provider->getAuthorizationUrl();

			$_SESSION['oauth2state'] = $this->provider->getState();

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
			$this->accessToken = $this->provider->getAccessToken('authorization_code', [
				'code'	=> $_GET['code']
			]);

			$_SESSION['access-token'] = $this->accessToken;
		}		
	}

	public function GetDetails()
	{
		$user = new User();

		// Get the basic user details.
		$request = $this->provider->getAuthenticatedRequest(
			'GET',
			"$this->mixerAPI/users/current",
			$this->accessToken
		);

		$response = $this->provider->getParsedResponse($request);

		if($response != null)
		{
			$user->mixerId = $response['id'];
			$user->mixer = $response['username'];
		}

		return $user;
	}

	public function GetFollowers($user)
	{
		$userId = $user->mixerId;
		$mixerUsers = implode(';', GduMinecraft::$Args['mixer-users']);
		$request = $this->provider->getAuthenticatedRequest(
			'GET',
			"$this->mixerAPI/users/$userId/follows?where=userId:in:$mixerUsers&fields=userId,token",
			$this->accessToken
		);

		$response = $this->provider->getParsedResponse($request);

		$user->following = array();
		foreach ($response as $followed)
		{
			array_push($user->following, $followed['userId']);
		}
	}
}
?>
