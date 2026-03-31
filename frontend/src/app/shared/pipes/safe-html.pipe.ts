import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import DOMPurify from 'dompurify';

@Pipe({
  name: 'safeHtml',
  standalone: true
})
export class SafeHtmlPipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {}

  transform(htmlString: string | undefined): SafeHtml {
    if (!htmlString) return '';
    
    // Sanitize string bằng DOMPurify trước, sau đó cho qua Angular DomSanitizer
    const sanitizedHtml = DOMPurify.sanitize(htmlString, {
      ALLOWED_TAGS: ['b', 'i', 'em', 'strong', 'a', 'p', 'br', 'ul', 'ol', 'li', 'span', 'div', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'u', 's', 'blockquote', 'pre', 'code'],
      ALLOWED_ATTR: ['href', 'target', 'class', 'style']
    });
    
    return this.sanitizer.bypassSecurityTrustHtml(sanitizedHtml);
  }
}
