import { UserImageType } from '@/types';

import screenSize from '@/constants/screenSize';

interface UserPicture {
  image: UserImageType;
  className?: string;
}

const UserPicture: React.FC<UserPicture> = ({ image, className }) => {
  return (
    <picture className={className}>
      <source media={`(max-width: ${screenSize.TABLET}px)`} type="image/webp" srcSet={image['160w']} />
      <source media={`(max-width: ${screenSize.DESKTOP}px)`} type="image/webp" srcSet={image['240w']} />
      <source media={`(max-width: ${screenSize.DESKTOP_BIC}px)`} type="image/webp" srcSet={image['320w']} />
      <source type="image/webp" srcSet={image['480w']} />
      <img src={image.fallback} alt="" />
    </picture>
  );
};

export default UserPicture;
