import { inject } from "@angular/core";
import { UrlTree, Router } from "@angular/router";
import { TokenService } from "../service/token.service";

export function authGuard(): boolean | UrlTree {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  const isLoggedIn = tokenService.isLoggedIn() ;

  return isLoggedIn ? true : router.createUrlTree(['/auth']);
}

export function reverseAuthGuard(): boolean | UrlTree {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  const isLoggedIn = tokenService.isLoggedIn() ;

  return isLoggedIn ? router.createUrlTree(['/inside']) : true;
}